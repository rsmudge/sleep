/*
 * Copyright 2002-2020 Raphael Mudge
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sleep.runtime;

import sleep.bridges.*;
import sleep.engine.Block;
import sleep.error.YourCodeSucksException;
import sleep.interfaces.Loadable;
import sleep.parser.Parser;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.*;

import sleep.taint.*;

/**
 * <p>The ScriptLoader is a convienence container for instantiating and managing ScriptInstances.</p>
 *
 * <h3>To load a script from a file and run it:</h3>
 *
 * <pre>
 * ScriptLoader   loader = new ScriptLoader();
 * ScriptInstance script = loader.loadScript("script.sl");
 *
 * script.runScript();
 * </pre>
 *
 * <p>The above will load the file script.sl and then execute it immediately.</p>
 *
 * <p>Installation of loadable bridges you create can also be managed by the ScriptLoader.</p>
 *
 * <p>A loadable bridge is installed into the language by adding it to a script loader class.  There are two types of
 * bridges.  The two types are specific and global bridges.</p>
 *
 * <p>The load and unload methods for a <b>specific bridge</b> are executed for every script load and unload, no matter
 * what.</p>
 *
 * <p>A <b>global bridge</b> is installed once for each script environment.  If scripts are sharing an environment there is
 * no sense in loading stuff into the environment more than once.  This is why global bridges exist.</p>
 *
 * <p>An example of adding a loadable bridge to a script loader:</p>
 *
 * <pre>
 * ScriptLoader loader = new ScriptLoader()
 * loader.addSpecificBridge(new MyLoadableBridge());
 * </pre>
 *
 * <h3>There is a difference between "loading" and "compiling" a script:</h3>
 *
 * <p>This class contains several methods to either load or compile a script.  Loading a script instantiates a script environment,
 * registers the script with the script loader, and registers all of the appropriate bridges with the script on top of compiling
 * the script.</p>
 *
 * <p>To compile a script means to produce a runnable Block of code.  On its own a Block is not really runnable as a script 
 * environment is needed.  For functions eval(), include(), etc.. it makes sense to compile a script as you may want to run
 * the block of code within the environment of the calling script.  Using the compile method saves on the overhead of unnecessary
 * script environment creation and bridge registration.</p>
 *
 * <h3>Management of Script Reloading</h3>
 *
 * <p>The ScriptInstance class has a an associateFile method to associate a source File object with a script.  The &amp;include function 
 * calls this method when a file is included into the current script context.  To check if any of the associated files has changed call 
 * hasChanged on the appropriate ScriptInstance.</P>
 *
 * <p>The ScriptLoader will automatically associate a source file with a ScriptInstance when a File object is passed to loadScript.  If
 * you choose to do some voodoo compiling scripts and managing your own cache (not necessary btw) then you will have to call associateFile
 * against any ScriptInstance you construct</p>
 *
 * <h3>Script Cache</h3>
 *
 * <p>The ScriptLoader mantains a cache of Blocks.  These are indexed by name and a timestamp of when they were created.  You may call the
 * touch method with the name and a timestamp to allow the ScriptLoader to invalidate the cache entry.  If you just load scripts from files
 * then the script cache will just work.  To disable the cache use <code>loader.setGlobalCache(false)</code>.</p>
 *
 * <p>Hopefully this helped to clarify things. :)</p>
 */
public class ScriptLoader
{
    /**
     * cache for parsed scripts mantained (optionally) by the script loader.
     */
    protected static Map BLOCK_CACHE = null;

    private Block retrieveCacheEntry(String name)
    {
       if (BLOCK_CACHE != null && BLOCK_CACHE.containsKey(name))
       {
          Object[] temp = (Object[])BLOCK_CACHE.get(name);

          return (Block)temp[0];
       }

       return null;
    }

    private static boolean isCacheHit(String name)
    {
       return BLOCK_CACHE != null && BLOCK_CACHE.containsKey(name);
    }

    /** nudge the cache with the last modified time of the specified script.  this call will delete the script from the cache if the lastModifiedTime > lastLoadTime */
    public void touch(String name, long lastModifiedTime)
    {
       if (BLOCK_CACHE != null && BLOCK_CACHE.containsKey(name))
       {
          Object[] temp   = (Object[])BLOCK_CACHE.get(name);
          long     loaded = ((Long)temp[1]).longValue();

          if (lastModifiedTime > loaded)
          {
             BLOCK_CACHE.remove(name);
          }
       }
    }

    /**
     * loaded scripts
     */
    protected LinkedList loadedScripts;

    /**
     * loaded scripts except referable by key
     */
    protected Map scripts;

    /**
     * global bridges
     */
    protected LinkedList bridgesg;

    /**
     * specific bridges
     */
    protected LinkedList bridgess;

    /**
     * path to search for jar files imported using [import * from: *] syntax
     */
    protected LinkedList paths;

    /**
     * initializes the script loader
     */
    public ScriptLoader()
    {
        loadedScripts = new LinkedList();
        scripts = new HashMap();
        bridgesg = new LinkedList();
        bridgess = new LinkedList();

        initDefaultBridges();
    }

    /**
     * The Sleep script loader can optionally cache parsed script files once they are loaded.  This is useful if you will have
     * several script loader instances loading the same script files in isolated objects.
     */
    public Map setGlobalCache(boolean setting)
    {
        if (setting && BLOCK_CACHE == null)
            BLOCK_CACHE = Collections.synchronizedMap(new HashMap());

        if (!setting)
            BLOCK_CACHE = null;

        return BLOCK_CACHE;
    }

    /**
     * method call to initialize the default bridges, if you want to change the default bridges subclass this class and
     * override this method
     */
    protected void initDefaultBridges()
    {
        addGlobalBridge(new BasicNumbers());
        addGlobalBridge(new BasicStrings());
        addGlobalBridge(new BasicUtilities());
        addGlobalBridge(new BasicIO());
        addGlobalBridge(new FileSystemBridge());
        addGlobalBridge(new DefaultEnvironment());
        addGlobalBridge(new DefaultVariable());
        addGlobalBridge(new RegexBridge());
        addGlobalBridge(new TimeDateBridge());
    }

    /**
     * A global bridge is loaded into an environment once and only once.  This way if the environment is shared among multiple
     * script instances this will save on both memory and script load time
     */
    public void addGlobalBridge(Loadable l)
    {
        bridgesg.add(l);
    }

    /**
     * A specific bridge is loaded into *every* script regardless of wether or not the environment is shared.  Useful for
     * modifying the script instance while it is being in processed. Specific bridges are the first thing that happens after
     * the script code is parsed
     */
    public void addSpecificBridge(Loadable l)
    {
        bridgess.add(l);
    }

    /**
     * Returns a HashMap with all loaded scripts, the key is a string which is just the filename, the value is a ScriptInstance
     * object
     */
    public Map getScriptsByKey()
    {
        return scripts;
    }

    /**
     * Determines wether or not the script is loaded by checking if the specified key exists in the script db.
     */
    public boolean isLoaded(String name)
    {
        return scripts.containsKey(name);
    }

    /**
     * Convienence method to return the script environment of the first script tht was loaded, returns null if no scripts are loaded
     */
    public ScriptEnvironment getFirstScriptEnvironment()
    {
        if (loadedScripts.size() > 0) 
        {
            ScriptInstance si = (ScriptInstance) loadedScripts.getFirst();
            return si.getScriptEnvironment();
        }

        return null;
    }

    /**
     * Returns a linked list of all loaded ScriptInstance objects
     */
    public LinkedList getScripts()
    {
        return loadedScripts;
    }

    /**
     * Process the newly loaded script.  Setup its name and load the bridges into the environment
     * assuming this hasn't been done before.  
     */
    protected void inProcessScript(String name, ScriptInstance si)
    {
        si.setName(name);

        Iterator i = bridgess.iterator();
        while (i.hasNext()) 
        {
            ((Loadable) i.next()).scriptLoaded(si);
        }

        // load the "global" bridges iff they need to be loaded again...
        if (si.getScriptEnvironment().getEnvironment().get("(isloaded)") != this) 
        {
            i = bridgesg.iterator();
            while (i.hasNext()) 
            {
                ((Loadable) i.next()).scriptLoaded(si);
            }
            si.getScriptEnvironment().getEnvironment().put("(isloaded)", this);
        }
    }

    /**
     * Load a serialized version of the script iff a serialized version exists, and its modification time is greater than the
     * modification time of the script.  Also handles the muss and fuss of reserializing the script if it has to reload the
     * script.  Personally I didn't find much of a startup time decrease when loading the scripts serialized versus parsing them
     * each time.  Theres a command 'bload' in the console to benchmark loading a script normally versus serialized.  Try it.
     *
     * @param script a file object pointing to the script file...
     */
    public ScriptInstance loadSerialized(File script, Hashtable env) throws IOException, ClassNotFoundException
    {
        File bin = new File(script.getAbsolutePath() + ".bin");

        if (bin.exists() && (!script.exists() || script.lastModified() < bin.lastModified())) 
        {
            return loadSerialized(script.getName(), new FileInputStream(bin), env);
        }

        ScriptInstance si = loadScript(script, env);
        saveSerialized(si);
        return si;
    }

    /**
     * Loads a serialized script from the specified input stream with the specified name
     */
    public ScriptInstance loadSerialized(String name, InputStream stream, Hashtable env) throws IOException, ClassNotFoundException
    {
        ObjectInputStream p = new ObjectInputStream(stream);
        Block block = (Block) p.readObject();
        return loadScript(name, block, env);
    }

    /**
     * Saves a serialized version of the compiled script to scriptname.bin.
     */
    public static void saveSerialized(ScriptInstance si) throws IOException
    {
        saveSerialized(si, new FileOutputStream(si.getName() + ".bin"));
    }

    /**
     * Saves a serialized version of the ScriptInstance si to the specified output stream
     */
    public static void saveSerialized(ScriptInstance si, OutputStream stream) throws IOException
    {
        ObjectOutputStream o = new ObjectOutputStream(stream);
        o.writeObject(si.getRunnableBlock());
    }

    /** creates a Sleep script instance using the precompiled code, name, and shared environment.  This function also
        processes the script using the global and specific bridges registered with this script loader.  No reference
        to the newly created script is kept by the script loader */
    public ScriptInstance loadScriptNoReference(String name, Block code, Hashtable env)
    {
        ScriptInstance si = new ScriptInstance(env);
        si.installBlock(code);
        inProcessScript(name, si);

        return si;
    }

    /** creates a Sleep script instance using the precompiled code, name, and shared environment.  This function also
        processes the script using the global and specific bridges registered with this script loader.  The script is
        also referened by this loader so it can be processed again (during the unload phase) when unloadScript is
        called. */
    public ScriptInstance loadScript(String name, Block code, Hashtable env)
    {
        ScriptInstance si = loadScriptNoReference(name, code, env);

        // add script to our loaded scripts data structure

        if (! name.equals("<interact mode>")) {
            loadedScripts.add(si);
            scripts.put(name, si);
        }

        return si;
    }

    /** loads the specified script */
    public ScriptInstance loadScript(String name, String code, Hashtable env) throws YourCodeSucksException
    {
        return loadScript(name, compileScript(name, code), env);
    }

    /** compiles a script using the specified stream as a source */
    public Block compileScript(String name, InputStream stream) throws YourCodeSucksException, IOException
    {
        if (isCacheHit(name)) 
        {
            stream.close();
            return retrieveCacheEntry(name);
        }

        StringBuffer code = new StringBuffer(8192);

        BufferedReader in = new BufferedReader(getInputStreamReader(stream));
        String s = in.readLine();
        while (s != null) 
        {
            code.append("\n");
            code.append(s);
            s = in.readLine();
        }

        in.close();
        stream.close();

        return compileScript(name, code.toString());
    }

    /**
     * compiles the specified script file
     */
    public Block compileScript(File file) throws IOException, YourCodeSucksException
    {
        touch(file.getAbsolutePath(), file.lastModified());
        return compileScript(file.getAbsolutePath(), new FileInputStream(file));
    }

    /**
     * compiles the specified script file
     */
    public Block compileScript(String fileName) throws IOException, YourCodeSucksException
    {
        return compileScript(new File(fileName));
    }

    /** compiles the specified script into a runnable block */
    public Block compileScript(String name, String code) throws YourCodeSucksException
    {
        if (isCacheHit(name)) 
        {
            return retrieveCacheEntry(name);
        } 
        else 
        {
            Parser temp = new Parser(name, code);
 
            if (TaintUtils.isTaintMode())
            {
               temp.setCodeFactory(new TaintModeGeneratedSteps());
            }

            temp.parse();

            if (BLOCK_CACHE != null)
            {
                BLOCK_CACHE.put(name, new Object[] { temp.getRunnableBlock(), new Long(System.currentTimeMillis()) });
            }

            return temp.getRunnableBlock();
        }
    }

    /** loads a script from the specified inputstream */
    public ScriptInstance loadScript(String name, InputStream stream) throws YourCodeSucksException, IOException
    {
        return loadScript(name, stream, null);
    }

    /** loads a script from the specified input stream using the specified hashtable as a shared environment */
    public ScriptInstance loadScript(String name, InputStream stream, Hashtable env) throws YourCodeSucksException, IOException
    {
        return loadScript(name, compileScript(name, stream), env);
    }

    /**
     * Loads the specified script file
     */
    public ScriptInstance loadScript(String fileName) throws IOException, YourCodeSucksException
    {
        return loadScript(new File(fileName), null);
    }

    /**
     * Loads the specified script file, uses the specified hashtable for the environment
     */
    public ScriptInstance loadScript(String fileName, Hashtable env) throws IOException, YourCodeSucksException
    {
        return loadScript(new File(fileName), env);
    }

    /**
     * Loads the specified script file, uses the specified hashtable for the environment
     */
    public ScriptInstance loadScript(File file, Hashtable env) throws IOException, YourCodeSucksException
    {
        ScriptInstance script = loadScript(file.getAbsolutePath(), new FileInputStream(file), env);
        script.associateFile(file);
        return script;
    }

    /**
     * Loads the specified script file
     */
    public ScriptInstance loadScript(File file) throws IOException, YourCodeSucksException
    {
        return loadScript(file, null);
    }

    /**
     * unload a script
     */
    public void unloadScript(String filename)
    {
        unloadScript((ScriptInstance) scripts.get(filename));
    }

    /**
     * unload a script
     */
    public void unloadScript(ScriptInstance script)
    {
        // clear the block cache of this script...
        if (BLOCK_CACHE != null) 
        {
            BLOCK_CACHE.remove(script.getName());
        }

        //
        // remove script from our loaded scripts data structure
        //
        loadedScripts.remove(script);
        scripts.remove(script.getName());

        //
        // the script must always be set to unloaded first and foremost!
        //
        script.setUnloaded();

        //
        // tell bridges script is going bye bye
        //
        Iterator i = bridgess.iterator();
        while (i.hasNext()) {
            Loadable temp = (Loadable) i.next();
            temp.scriptUnloaded(script);
        }

        i = bridgesg.iterator();
        while (i.hasNext()) {
            Loadable temp = (Loadable) i.next();
            temp.scriptUnloaded(script);
        }
    }

    /**
     * A convienence method to determine the set of scripts to "unload" based on a passed in set of scripts that are currently
     * configured.  The configured scripts are compared to the loaded scripts.  Scripts that are loaded but not configured are
     * determined to be in need of unloading.  The return Set contains String objects of the script names.  The passed in Set is
     * expected to be the same thing (a bunch of Strings).
     */
    public Set getScriptsToUnload(Set configured)
    {
        Set unload, loaded;
        unload = new LinkedHashSet();

        // scripts that are currently loaded and active...
        loaded = scripts.keySet();

        // scripts that need to be unloaded...
        unload.addAll(loaded);
        unload.removeAll(configured);

        return unload;
    }

    /**
     * A convienence method to determine the set of scripts to "load" based on a passed in set of scripts that are currently
     * configured.  The configured scripts are compared to the loaded scripts.  Scripts that are configured but not loaded
     * are determined to be in need of loading.  The return Set contains String objects of the script names.  The passed in
     * Set is expected to be the same thing (a bunch of Strings).
     */
    public Set getScriptsToLoad(Set configured)
    {
        Set load, loaded;
        load = new LinkedHashSet();

        // scripts that are currently loaded and active...
        loaded = scripts.keySet();

        // scripts that need to be unloaded...
        load.addAll(configured);
        load.removeAll(loaded);

        return load;
    }

    /**
     * Java by default maps characters from an 8bit ascii file to an internal 32bit unicode representation.  How this mapping is done is called a character set encoding.  Sometimes this conversion can frustrate scripters making them say "hey, I didn't put that character in my script".  You can use this option to ensure sleep disables charset conversions for scripts loaded with this script loader
     */
    public void setCharsetConversion(boolean b)
    {
        disableConversions = !b;
    }

    public boolean isCharsetConversions()
    {
        return !disableConversions;
    }

    protected boolean disableConversions = false;
    private static CharsetDecoder decoder = null;
    private String charset = null;

    public String getCharset()
    {
        return charset;
    }

    /**
     * If charset conversion is enabled and charset is set, then the stream will be read using specified charset.
     *
     * @param charset The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    private InputStreamReader getInputStreamReader(InputStream in)
    {
        if (disableConversions) {
            if (decoder == null)
                decoder = new NoConversion();

            return new InputStreamReader(in, decoder);
        }

        if (charset != null) {
            try {
                return new InputStreamReader(in, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return new InputStreamReader(in);
    }

    /**
     * Java likes to convert characters inside of a loaded script into something else.  This prevents that if the app
     * developer chooses to flag that option
     */
    private static class NoConversion extends CharsetDecoder
    {
        public NoConversion()
        {
            super(null, 1.0f, 1.0f);
        }

        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out)
        {
            int mark = in.position();
            try {
                while (in.hasRemaining()) {
                    if (!out.hasRemaining())
                        return CoderResult.OVERFLOW;

                    int index = (int) in.get();
                    if (index >= 0) {
                        out.put((char) index);
                    } else {
                        index = 256 + index;
                        out.put((char) index);
                    }
                    mark++;
                }
                return CoderResult.UNDERFLOW;
            }
            finally { in.position(mark); }
        }
    }
}
