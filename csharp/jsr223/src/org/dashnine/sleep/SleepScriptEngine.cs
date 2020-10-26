/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 * Copyright 2020 Sebastian Ritter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/* Derived from code created by A. Sundararajan (sundararajana@dev.java.net) at Sun 
   What remains of this class is almost unrecognizable from the original. 
 
   The license info was removed as none of the original code remains in this file.  
*/
using System;
using java = biz.ritter.javapi;

using  javax.script; 
using  java.lang.reflect; 
using  java.io; 
using  java.util;

using  sleep.bridges; 
using  sleep.engine; 
using  sleep.interfaces; 
using  sleep.runtime;

using  sleep.error;

namespace org.dashnine.sleep{

public class SleepScriptEngine : AbstractScriptEngine
{
    // my factory, may be null
    private ScriptEngineFactory factory;

    private ScriptLoader    loader;
    private Hashtable       sharedEnvironment;
    private ScriptVariables variables;

    public SleepScriptEngine()
    {
        loader = new ScriptLoader();
        sharedEnvironment = new Hashtable();
    }

    /** executes a console command */
    public Object eval(String str, ScriptContext ctx) //throws ScriptException
    {
        ScriptInstance script = compile(str, ctx);
        return evalScript(script, ctx);
    }

    /** executes a script */
    public Object eval(Reader reader, ScriptContext ctx) //throws ScriptException
    {
        ScriptInstance script = compile(readFully(reader), ctx);
        return evalScript(script, ctx);
    }

    private Object evalScript(ScriptInstance script, ScriptContext context)
    {
        /* install global bindings */
        Bindings global = context.getBindings(ScriptContext.GLOBAL_SCOPE);

        if (global != null)
        {
           Iterator i = global.entrySet().iterator();
           while (i.hasNext())
           {
              Map.Entry value = (Map.Entry)i.next();
              script.getScriptVariables().putScalar("$" + value.getKey().toString(), ObjectUtilities.BuildScalar(true, value.getValue()));
           }
        }

        /* install local bindings */
        Bindings local = context.getBindings(ScriptContext.ENGINE_SCOPE);
        Map locals = new HashMap();

        if (local != null)
        {
           Iterator i = local.entrySet().iterator();
           while (i.hasNext())
           {
              Map.Entry value = (Map.Entry)i.next();
              locals.put("$" + value.getKey().toString(), ObjectUtilities.BuildScalar(true, value.getValue())  );
           }
        }

        if (locals.get("$" + ScriptEngine.FILENAME) != null)
        {
           script.getScriptVariables().putScalar("$__SCRIPT__", (Scalar)locals.get("$" + ScriptEngine.FILENAME));
        }        

        if (locals.get("$" + ScriptEngine.ARGV) != null)
        {
           script.getScriptVariables().putScalar("@ARGV", (Scalar)locals.get("$" + ScriptEngine.ARGV));
        }

        return SleepUtils.runCode(script.getRunnableScript(), "eval", script, SleepUtils.getArgumentStack(locals)).objectValue();
    }

    private static class WarningWatcher : RuntimeWarningWatcher
    {
        protected ScriptContext context;

        public WarningWatcher(ScriptContext _context)
        {
           context = _context;
        }

        public void processScriptWarning(ScriptWarning warning)    
        {
           SystemJ.outJ.println(warning.toString());
        }
    }

    private ScriptInstance compile(String text, ScriptContext context) //throws ScriptException
    {
        try
        {
           ScriptInstance script = loader.loadScript("eval", text, sharedEnvironment);
           script.addWarningWatcher(new WarningWatcher(context));
           return script;
        }
        catch (YourCodeSucksException ex)
        {
           throw new ScriptException(ex.formatErrors());
        }
    }

    public ScriptEngineFactory getFactory()
    {
	lock (this)
        {
	    if (factory == null)
            {
	    	factory = new SleepScriptEngineFactory();
	    }
        }
	return factory;
    }

    public Bindings createBindings()
    {
        return new SimpleBindings();
    }

    // package-private methods
    void setFactory(ScriptEngineFactory factory)
    {
        this.factory = factory;
    }

    private String readFully(Reader reader) //throws ScriptException 
    {
        StringBuffer code = new StringBuffer(8192);

        try
        { 
           BufferedReader inJ = new BufferedReader(reader);
           String s = inJ.readLine();   
           while (s != null)
           {
               code.append("\n");
               code.append(s);
               s = inJ.readLine();
           }
   
           inJ.close();
        }
        catch (Exception ex) { }

        return code.toString();
    }
}
}