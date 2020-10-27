/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
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
using System;
using java = biz.ritter.javapi;

using  sleep.interfaces;
using  sleep.engine;
using  sleep.error;
using  sleep.parser;
using  sleep.runtime;
using  sleep.bridges;

namespace sleep.console{

/**
 * <p>The ConsoleImplementation is the "engine" behind the sleep console.  To use the sleep console in your application use 
 * the following steps:</p>
 * 1. Instantiate the console implementation
 * <br>
 * <br><code>ConsoleImplementation console;</code>
 * <br><code>console = new ConsoleImplementation(environment, variables, loader);</code>
 * <br>
 * <br>2. Install your implementation of sleep.console.ConsoleProxy into the console
 * <br>
 * <br><code>console.setProxy(new MyConsoleProxy());</code>
 * <br>
 * <br>3. Start the Read, Parse, Print Loop in the console
 * <br>
 * <br><code>console.rppl(); // starts the console</code>
 * 
 * <p>When embedding the console reusing the object of an already quitted console is not
 * only allowed but it is also recommended.  When a user quits the console with the quit command
 * the console proxy is set to a dummy console that does not output anything.  To restart
 * a quitted console just set the appropriate proxy again and call the <code>rppl()</code> method.</P>
 *
 * @see sleep.console.ConsoleProxy
 * @see sleep.runtime.ScriptLoader
 * @see sleep.interfaces.Variable
 */
public class ConsoleImplementation : RuntimeWarningWatcher, Loadable, ConsoleProxy
{
   /** the *active* script... */
   private ScriptInstance script; 

   /** the user installed console proxy, defining all input/output for the console */
   private ConsoleProxy myProxy; 

   /** the script environment with all of the installed functions, predicates, and environments */
   private java.util.Hashtable<Object,Object>        sharedEnvironment; 

   /** the shared variable container for all scripts, assuming variables are being shared */
   private Variable         sharedVariables; 

   /** the script loader */
   private ScriptLoader     loader; 

   /** our using  manager */
   private ImportManager    imports;

   /** Creates an implementation of the sleep console.  The implementation created by this constructor is isolated from your 
       applications environment.  Any scripts loaded via this console will have only the default bridges.  */
   public ConsoleImplementation()
   {
      this(new java.util.Hashtable<Object,Object>(), new DefaultVariable(), new ScriptLoader());
   }

   /** Creates an implementation of the sleep console that shares what your application is already using.  Any of the 
     * parameters can be null. 
     *
     * <p><font color="red"><b>Warning!</b></font> If you choose to use the Sleep console in your application with this constructor,
     * be aware that even if you don't specify a set of variables or an environment for scripts to share that they will all end up 
     * sharing something as the sleep console will create and install its own environment or variables if you don't specify 
     * something.</p>
     *
     * @param _sharedEnvironment the environment contains all of the bridges (functions, predicates, and environments)
     * @param _sharedVariables the Variable class is a container for Scalar variables with global, local, and script specific scope
     * @param _loader the Script Loader is a container for managing all of the currently loaded scripts
     */
   public ConsoleImplementation(java.util.Hashtable<Object,Object> _sharedEnvironment, Variable _sharedVariables, ScriptLoader _loader)
   {
      if (_sharedEnvironment == null)
         _sharedEnvironment = new Hashtable();

      if (_sharedVariables == null)
         _sharedVariables = new DefaultVariable();

      if (_loader == null)
         _loader = new ScriptLoader();

      sharedEnvironment = _sharedEnvironment;
      sharedVariables   = _sharedVariables;
      loader            = _loader;
      loader.addSpecificBridge(this);

      setProxy(this);
   }

   /** Returns the current console proxy being used */
   public ConsoleProxy getProxy()
   {
      return myProxy;
   }

   /** Sets up the implementation of the consoles input/output facilities */
   public void setProxy(ConsoleProxy p)
   {
      myProxy = p;
   }

   /** Dummy implementation, does nothing really. */
   public void consolePrint(String m) { }

   /** Dummy implementation, always returns null. */
   public String consoleReadln() { return null; }

   /** Dummy implementation, does nothing. */
   public void consolePrintln(Object m) { }

   private bool _interact = true; // are we in interact mode?

   /** starts the console */
   public void rppl() //throws IOException
   {
       getProxy().consolePrintln(">> Welcome to the Sleep scripting language");

       _interact = false;

       String input;
       java.lang.StringBuffer code   = new java.lang.StringBuffer();
       String       repeat = ""; 

       while (true)
       {
          if (!_interact)
             getProxy().consolePrint("> ");

          input = getProxy().consoleReadln();

          if (_interact)
          {
             if (input == null || input.equals("done"))
             {
                _interact = false;
             }
             else if (input.equals("."))
             { 
                if (code.length() == 0)
                {
                   eval(repeat, repeat);
                }
                else
                {
                   eval(code.toString(), code.toString());
                   repeat = code.toString();
                   code   = new StringBuffer();                                
                }
             }
             else
             {
                code.append(input + "\n");
             }
          }
          else if (input != null)
          {
             String command, args, filter;
             if (input.indexOf(' ') > -1)
             {
                command = input.substring(0, input.indexOf(' '));
                args    = input.substring(command.length() + 1, input.length());
             }
             else
             {
                command = input;
                args    = null;
             }

             if (command.equals("env"))
             {
                if (args != null && args.indexOf(' ') > -1)
                {
                   filter = args.substring(args.indexOf(' ') + 1, args.length());
                   args   = args.substring(0, args.indexOf(' '));
                }
                else
                {
                   filter = null;
                }

                env(args, filter);
             }
             else if (command.equals("version"))
             {
                getProxy().consolePrintln(SleepUtils.SLEEP_VERSION + " (" + SleepUtils.SLEEP_RELEASE + ")");
             }
             else if (command.equals("help") && args != null)
             {
                help(args);
             }
             else if (command.equals("help"))
             {
                help();
             }
             else if (command.equals("interact"))
             {
                _interact();
             }
             else if (command.equals("list"))
             {
                list();
             }
             else if (command.equals("debug") && args != null) 
             {
                String[] splits = args.split(" ");
                if (splits.length == 2)
                {
                   debug(splits[0], Integer.parseInt(splits[1]));
                }
                else if (splits.length == 1)
                {
                   debug(null, Integer.parseInt(splits[0]));
                }
                else
                {
                   getProxy().consolePrintln("Invalid usage: debug [script] <level>");
                }
             }
             else if (command.equals("load") && args != null)
             {
                load(args);
             }
             else if (command.equals("tree") && (args != null || script != null))
             {
                tree(args);
             }
             else if (command.equals("unload") && args != null)
             {
                unload(args);
             }
             else if (command.equals("x") && args != null)
             {
                Scalar value = eval("return " + args + ";", args);
                if (value != null) { getProxy().consolePrintln(value + ""); }
             }
             else if (command.equals("?") && args != null)
             {
                Scalar value = eval("return iff(" + args + ", 'true', 'false');", args);
                if (value != null) { getProxy().consolePrintln(value + ""); }
             }
             else if (command.equals("quit") || command.equals("exit") || command.equals("done"))
             {
                getProxy().consolePrintln("Good bye!");
                setProxy(this);
                break;
             }
             else if (command.trim().length() > 0)
             {
                getProxy().consolePrintln("Command '"+command+"' not understood.  Type 'help' if you need it");
             } 
          }
          else
          {
             getProxy().consolePrintln("Good bye!");
             setProxy(this);
             break;
          }
      }

      _interact = true;
   }

   private void help()
   {
       getProxy().consolePrintln("debug [script] <level>");
       getProxy().consolePrintln("env [functions/other] [regex filter]");
       getProxy().consolePrintln("help [command]");
       getProxy().consolePrintln("interact");
       getProxy().consolePrintln("list");
       getProxy().consolePrintln("load <file>");
       getProxy().consolePrintln("unload <file>");
       getProxy().consolePrintln("tree [key]");
       getProxy().consolePrintln("quit");
       getProxy().consolePrintln("version");
       getProxy().consolePrintln("x <expression>");
       getProxy().consolePrintln("? <predicate expression>");
   }

   private void help(String command)
   {
       if (command.equals("debug"))
       {
          getProxy().consolePrintln("debug [script] <level>");
          getProxy().consolePrintln("   sets the debug level for the specified script");
          getProxy().consolePrintln("   1 - show critical errors");
          getProxy().consolePrintln("   2 - show warnings");
          getProxy().consolePrintln("   4 - strict mode, complain about non-declared variables");
          getProxy().consolePrintln("   8 - trace all function calls");
          getProxy().consolePrintln("   to combine options, add their numbers together");
       }
       else if (command.equals("env"))
       {
          getProxy().consolePrintln("env [functions/other] [regex filter]");
          getProxy().consolePrintln("   dumps the shared environment, filters output with specified regex");
       }
       else if (command.equals("interact"))
       {
          getProxy().consolePrintln("interact");
          getProxy().consolePrintln("   enters the console into interactive mode.");
       }
       else if (command.equals("list"))
       {
          getProxy().consolePrintln("list");
          getProxy().consolePrintln("   lists all of the currently loaded scripts");
       }
       else if (command.equals("load"))
       {
          getProxy().consolePrintln("load <file>");
          getProxy().consolePrintln("   loads a script file into the script loader");
       }
       else if (command.equals("unload"))
       {
          getProxy().consolePrintln("unload <file>");
          getProxy().consolePrintln("   unloads a script file from the script loader");
       }
       else if (command.equals("tree"))
       {
          getProxy().consolePrintln("tree [key]");
          getProxy().consolePrintln("   displays the Abstract Syntax Tree for the specified key");
       }
       else if (command.equals("quit"))
       {
          getProxy().consolePrintln("quit");
          getProxy().consolePrintln("   stops the console");
       }
       else if (command.equals("version"))
       {
          getProxy().consolePrintln("version");
          getProxy().consolePrintln("   display the current Sleep version");
       }
       else if (command.equals("x"))
       {
          getProxy().consolePrintln("x <expression>");
          getProxy().consolePrintln("   evaluates a sleep expression and displays the value");
       }
       else if (command.equals("?"))
       {
          getProxy().consolePrintln("? <predicate expression>");
          getProxy().consolePrintln("   evaluates a sleep predicate expression and displays the truth value");
       }
       else
       {
          getProxy().consolePrintln("help [command]");
          getProxy().consolePrintln("   displays a help message for the specified command");
       }
   }

   private void load(String file)
   {
       try
       {
          ScriptInstance script = loader.loadScript(file, sharedEnvironment);

          if (System.getProperty("sleep.debug") != null)
          {
             script.setDebugFlags(Integer.parseInt(System.getProperty("sleep.debug")));
          }

          script.runScript();
       }
       catch (YourCodeSucksException yex)
       {
          processScriptErrors(yex);
       }
       catch (java.lang.Exception ex)
       {
          getProxy().consolePrintln("Could not load script " + file + ": " + ex.getMessage());
       }
   }

   private String getFullScript(String name)
   {
       if (loader.getScriptsByKey().containsKey(name))
       {
          return name;
       }

       Iterator i = loader.getScripts().iterator();
       while (i.hasNext())
       {
          ScriptInstance script = (ScriptInstance)i.next();
          File temp = new File(script.getName());
 
          if (temp.getName().equals(name))
          {
             return temp.getAbsolutePath();
          }
       }

       return name;
   }

   private void unload(String file)
   {
       try
       {
          loader.unloadScript(getFullScript(file));
       }
       catch (Exception ex)
       {
          getProxy().consolePrintln("Could not unloaded script " + file + ": " + ex.getMessage());
       }
   }

   private void list()
   {
       Iterator i = loader.getScripts().iterator();
       while (i.hasNext())
       {
          ScriptInstance temp = (ScriptInstance)i.next();
          getProxy().consolePrintln(temp.getName());
       }
   }

   private void env(String type, String filter)
   {
       Iterator i = sharedEnvironment.keySet().iterator();
       while (i.hasNext())
       {
          Object temp = i.next();
          
          if ( (type == null) || 
               (type.equals("functions") && temp.toString().charAt(0) == '&') ||
               (type.equals("other") && temp.toString().charAt(0) != '&') 
             )
          {
             if (filter == null || java.util.regex.Pattern.matches(".*?" + filter + ".*", sharedEnvironment.get(temp).toString()))
             {
                getProxy().consolePrintln(align(temp.toString(), 20) + " => " + sharedEnvironment.get(temp));
             }
          }
       }
   }

   private String align(String text, int to)
   {
       StringBuffer temp = new StringBuffer(text);
       while (temp.length() < to)
       {
          temp.append(" ");
       }

       return temp.toString();
   }

   private void tree(String item)
   {
       if (item == null)
       {
          getProxy().consolePrintln(script.getRunnableBlock().toString());
       }
       else if (item.charAt(0) == '&' || item.charAt(0) == '$')
       {
          if (sharedEnvironment != null && sharedEnvironment.get(item) is SleepClosure)
          {
             SleepClosure temp = (SleepClosure)sharedEnvironment.get(item);
             getProxy().consolePrintln(temp.getRunnableCode());
          }
          else
          {
             getProxy().consolePrintln("Could not find code block "+item+" to print tree of");
          }
       }
       else
       {
          Map temp = loader.getScriptsByKey();

          if (temp.get(getFullScript(item)) != null)
          {
             getProxy().consolePrintln(((ScriptInstance)temp.get(getFullScript(item))).getRunnableBlock());
          }
          else
          {
             getProxy().consolePrintln("Could not find script "+item+" to print tree of");
          }
       }
   }

   private void debug(String item, int level)
   {
       if (item == null)
       {
          System.setProperty("sleep.debug", ""+level);
          getProxy().consolePrintln("Default debug level set");
       }
       else
       {
          Map temp = loader.getScriptsByKey();

          if (temp.get(getFullScript(item)) != null)
          {
             ((ScriptInstance)temp.get(getFullScript(item))).setDebugFlags(level);
             getProxy().consolePrintln("Debug level set for "+item);
          }
          else
          {
             getProxy().consolePrintln("Could not find script "+item+" to set debug level for");
          }
       }
   }

   private void interact()
   {
       _interact = true;
       getProxy().consolePrintln(">> Welcome to interactive mode.");
       getProxy().consolePrintln("Type your code and then '.' on a line by itself to execute the code.");
       getProxy().consolePrintln("Type Ctrl+D or 'done' on a line by itself to leave interactive mode.");
   }

   private Scalar eval (String expression, String original)
   {
       try
       {
          Parser parser = new Parser("eval", expression.toString(), imports);
          imports = parser.getImportManager();
          parser.parse();
          Block parsed = parser.getRunnableBlock();

          script = loader.loadScript("<interact mode>", parsed, sharedEnvironment);

          if (System.getProperty("sleep.debug") != null)
          {
             script.setDebugFlags(Integer.parseInt(System.getProperty("sleep.debug")));
          }

          return script.runScript();
       }
       catch (YourCodeSucksException yex)
       {
          processScriptErrors(yex);
       }
       catch (Exception ex)
       {
          getProxy().consolePrintln("error with " + original + ": " + ex.toString());
       } 

       return null;
   }

   /** a convienence method that formats and writes each syntax error to the proxy output */
   public void processScriptErrors(YourCodeSucksException ex)
   {
      getProxy().consolePrint(ex.formatErrors());
   }

   public void processScriptWarning(ScriptWarning warning)
   {
      getProxy().consolePrintln(warning.toString());
   }     

   public void scriptLoaded(ScriptInstance script)
   {
      if (! script.getName().equals("<interact mode>") && !interact)
         getProxy().consolePrintln(script.getName() + " loaded successfully.");

      script.addWarningWatcher(this);
      script.setScriptVariables(new ScriptVariables(sharedVariables));
   }

   public void scriptUnloaded(ScriptInstance script)
   {
      getProxy().consolePrintln(script.getName() + " has been unloaded");
   }
}
}