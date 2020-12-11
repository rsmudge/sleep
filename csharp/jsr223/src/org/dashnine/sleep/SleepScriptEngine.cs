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
using javax = biz.ritter.javapix;

using  sleep.bridges; 
using  sleep.engine; 
using  sleep.interfaces; 
using  sleep.runtime;
using  sleep.error;

/// PORT to VampireApi
namespace biz.ritter.javapix.script {

    public interface ScriptEngineFactory {}
    public interface ScriptEngine{}
    public class AbstractScriptEngine : ScriptEngine {}
    public interface ScriptContext{}

    public interface Bindings{}
    public class SimpleBindings : Bindings{}
}

namespace org.dashnine.sleep{

public class SleepScriptEngine : javax.script.AbstractScriptEngine
{
    // my factory, may be null
    private javax.script.ScriptEngineFactory factory;

    private ScriptLoader    loader;
    private java.util.Hashtable<Object,Object>       sharedEnvironment;
    private ScriptVariables variables;

    public SleepScriptEngine()
    {
        loader = new ScriptLoader();
        sharedEnvironment = new java.util.Hashtable<Object,Object>();
    }

    /** executes a console command */
    public Object eval(String str, javax.script.ScriptContext ctx) //throws ScriptException
    {
        ScriptInstance script = compile(str, ctx);
        return evalScript(script, ctx);
    }

    /** executes a script */
    public Object eval(java.io.Reader reader, javax.script.ScriptContext ctx) //throws ScriptException
    {
        ScriptInstance script = compile(readFully(reader), ctx);
        return evalScript(script, ctx);
    }

    private Object evalScript(ScriptInstance script, javax.script.ScriptContext context)
    {
        /* install global bindings */
        javax.script.Bindings global = context.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE);

        if (global != null)
        {
           java.util.Iterator<Object> i = global.entrySet().iterator();
           while (i.hasNext())
           {
              java.util.MapNS.Entry<Object,Object> value = (java.util.MapNS.Entry<Object,Object>)i.next();
              script.getScriptVariables().putScalar("$" + value.getKey().toString(), ObjectUtilities.BuildScalar(true, value.getValue()));
           }
        }

        /* install local bindings */
        javax.script.Bindings local = context.getBindings(javax.script.ScriptContext.ENGINE_SCOPE);
        java.util.Map<Object,Object> locals = new java.util.HashMap<Object,Object>();

        if (local != null)
        {
           java.util.Iterator<Object> i = local.entrySet().iterator();
           while (i.hasNext())
           {
              java.util.MapNS.Entry<Object,Object> value = (java.util.MapNS.Entry<Object,Object>)i.next();
              locals.put("$" + value.getKey().toString(), ObjectUtilities.BuildScalar(true, value.getValue())  );
           }
        }

        if (locals.get("$" + javax.script.ScriptEngine.FILENAME) != null)
        {
           script.getScriptVariables().putScalar("$__SCRIPT__", (Scalar)locals.get("$" + javax.script.ScriptEngine.FILENAME));
        }        

        if (locals.get("$" + javax.script.ScriptEngine.ARGV) != null)
        {
           script.getScriptVariables().putScalar("@ARGV", (Scalar)locals.get("$" + javax.script.ScriptEngine.ARGV));
        }

        return SleepUtils.runCode(script.getRunnableScript(), "eval", script, SleepUtils.getArgumentStack(locals)).objectValue();
    }

    private class WarningWatcher : RuntimeWarningWatcher
    {
        protected javax.script.ScriptContext context;

        public WarningWatcher(javax.script.ScriptContext _context)
        {
           context = _context;
        }

        public void processScriptWarning(ScriptWarning warning)    
        {
           java.lang.SystemJ.outJ.println(warning.toString());
        }
    }

    private ScriptInstance compile(String text, javax.script.ScriptContext context) //throws ScriptException
    {
        try
        {
           ScriptInstance script = loader.loadScript("eval", text, sharedEnvironment);
           script.addWarningWatcher(new WarningWatcher(context));
           return script;
        }
        catch (YourCodeSucksException ex)
        {
           throw new javax.script.ScriptException(ex.formatErrors());
        }
    }

    public javax.script.ScriptEngineFactory getFactory()
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

    public javax.script.Bindings createBindings()
    {
        return new javax.script.SimpleBindings();
    }

    // package-private methods
    internal void setFactory(javax.script.ScriptEngineFactory factory)
    {
        this.factory = factory;
    }

    private String readFully(java.io.Reader reader) //throws ScriptException 
    {
        java.lang.StringBuffer code = new java.lang.StringBuffer(8192);

        try
        { 
           java.io.BufferedReader inJ = new java.io.BufferedReader(reader);
           String s = inJ.readLine();   
           while (s != null)
           {
               code.append("\n");
               code.append(s);
               s = inJ.readLine();
           }
   
           inJ.close();
        }
        catch (java.lang.Exception ex) { }

        return code.toString();
    }
}
}