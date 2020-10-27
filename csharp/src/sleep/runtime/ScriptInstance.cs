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

using  sleep.runtime;
using  sleep.engine;
using  sleep.bridges;
using  sleep.interfaces;
using  sleep.error;
using  sleep.parser;

namespace sleep.runtime{


/** Every piece of information related to a loaded script.  This includes the scripts runtime environment, code in compiled 
  * form, variable information, and listeners for runtime issues.
  */
  [Serializable]
public class ScriptInstance : java.io.Serializable, java.lang.Runnable
{
    /** the name of this script */
    protected String  name   = "Script";

    /** true by default, indicates wether or not the script is loaded.  Once unloaded this variable must be flagged to false so
        the bridges know data related to this script is stale */
    protected bool loaded; 

    /** A list of listeners watching for a runtime error */
    protected java.util.LinkedList<Object> watchers = new java.util.LinkedList<Object>(); 

    /** The script environment which contains all of the runtime info for a script */
    protected ScriptEnvironment environment;

    /** The script variables which contains all of the variable information for a script */
    protected ScriptVariables   variables;

    /** The compiled sleep code for this script, the ScriptLoader will set this value upon loading a script. */
    protected SleepClosure      script;

    /** debug should be absolutely quiet, never fire any runtime warnings */
    public static readonly int DEBUG_NONE          = 0;

    /** fire runtime warnings for all critical flow interrupting errors */
    public static readonly int DEBUG_SHOW_ERRORS   = 1;

    /** fire runtime warnings for anything flagged for retrieval with checkError() */
    public static readonly int DEBUG_SHOW_WARNINGS = 2;

    /** fire runtime warning whenever an undeclared variable is fired */
    public static readonly int DEBUG_REQUIRE_STRICT = 4;

    /** fire a runtime warning describing each function call */
    public static readonly int DEBUG_TRACE_CALLS    = 8;

    /** forces function call tracing to occur (for the sake of profiling a script) but supresses
        all runtime warnings as a result of the tracing */
    public static readonly int DEBUG_TRACE_PROFILE_ONLY = 8 | 16;

    /** users shouldn't need to flag this, it is just a general method of saying we're suppressing
        trace messages... */
    protected static readonly int DEBUG_TRACE_SUPPRESS = 16;

    /** throw exceptions for anything flagged for retrieval with checkError() */
    public static readonly int DEBUG_THROW_WARNINGS = 2 | 32;

    /** fire a runtime warning describing each predicate decision made */
    public static readonly int DEBUG_TRACE_LOGIC = 64;

    /** trace the passage of tainted data */
    public static readonly int DEBUG_TRACE_TAINT = 128;

    /** track all of the flagged debug options for this script (set to DEBUG_SHOW_ERRORS by default) */
    protected int debug = DEBUG_SHOW_ERRORS;

    /** track the time this script was loaded */
    protected long loadTime = System.currentTimeMillis();

    /** list of source files associated with this script (to account for &include) */
    protected java.util.List<Object> sourceFiles = new LinkedList<Object>();

    /** associates the specified source file with this script */
    public void associateFile(java.io.File f)
    {
       if (f.exists())
       {
          sourceFiles.add(f);
       }
    }

    /** this script instance checks if (to the best of its knowledge) any of its source files have changed */
    public bool hasChanged()
    {
       java.util.Iterator<Object> i = sourceFiles.iterator();
       while (i.hasNext())
       {
          java.io.File temp = (Fjava.io.ile)i.next();
          if (temp.lastModified() > loadTime)
          {
             return true;
          }
       }
   
       return false;
    }

    /** set the debug flags for this script */
    public void setDebugFlags(int options)
    {
        debug = options;
    }

    /** retrieve the debug flags for this script */
    public int getDebugFlags()  
    {
        return debug;
    }

    /** Constructs a script instance, if the parameter is null a default implementation will be used.
        By specifying the same shared Hashtable container for all scripts, such scripts can be made to
        environment information */
    public ScriptInstance(java.util.Hashtable<Object,Object> environmentToShare)
    {
        this((Variable)null, environmentToShare);
    }

    /** Constructs a script instance, if either of the parameters are null a default implementation will be used.
        By specifying the same shared Variable and Hashtable containers for all scripts, scripts can be made to
        share variables and environment information */
    public ScriptInstance(Variable varContainerToUse, java.util.Hashtable<Object,Object> environmentToShare)
    {
        if (environmentToShare == null)
        {
           environmentToShare = new java.util.Hashtable();
        }

        if (varContainerToUse == null)
        {
           variables = new ScriptVariables();
        }
        else
        {
           variables = new ScriptVariables(varContainerToUse);
        }

        environment = new ScriptEnvironment(environmentToShare, this);

        loaded = true;
    }

    /** Install a block as the compiled script code */ 
    public void installBlock(Block _script)
    {
        script = new SleepClosure(this, _script);
    }

    /** Constructs a new script instance */
    public ScriptInstance()
    {
        this((Variable)null, (Hashtable)null);
    }

    /** Returns this scripts runtime environment */
    public ScriptEnvironment getScriptEnvironment()
    {
        return environment;
    }

    /** Sets the variable container to be used by this script */
    public void setScriptVariables(ScriptVariables v)
    {
        variables = v;
    }

    /** Returns the variable container used by this script */
    public ScriptVariables getScriptVariables()
    {
        return variables;
    }
    
    /** Returns the name of this script (typically a full pathname) as a String */
    public String getName()
    {
        return name;
    }

    /** Sets the name of this script */
    public void setName(String sn)
    {
        name = sn;
    }

    /** Executes this script, should be done first thing once a script is loaded */
    public Scalar runScript()
    {
       return SleepUtils.runCode(script, null, this, null);
    }
 
    /** A container for Sleep strack trace elements. */
    [Serializable]
    public class SleepStackElement : java.io.Serializable
    {
        public String sourcefile;
        public String description;
        public int    lineNumber;

        public String toString()
        {
           return "   " + (new File(sourcefile).getName()) + ":" + lineNumber + " " + description;
        }
    }
 
    /** Records a stack frame into this environments stack trace tracker thingie. */
    public void recordStackFrame(String description, String source, int lineNumber)
    {
       List strace = (List)getScriptEnvironment().getEnvironment().get("%strace%");

       if (strace == null) 
       {
          strace = new LinkedList();
          getScriptEnvironment().getEnvironment().put("%strace%", strace);
       }

       SleepStackElement stat = new SleepStackElement();
       stat.sourcefile  = source;
       stat.description = description;
       stat.lineNumber  = lineNumber;

       strace.add(0, stat);
    }

    /** return the current working directory value associated with this script. */
    public java.io.File cwd()
    {
       if (!getMetadata().containsKey("__CWD__"))
       {
          chdir(null);
       }

       return (java.io.File)getMetadata().get("__CWD__");
    }

    /** sets the current working directory value for this script */
    public void chdir(java.io.File f)
    {
       if (f == null)
       {
           f = new java.io.File("");
       }

       getMetadata().put("__CWD__", f.getAbsoluteFile());
    }

    /** Records a stack frame into this environments stack trace tracker thingie. */
    public void recordStackFrame(String description, int lineNumber)
    {
       recordStackFrame(description, getScriptEnvironment().getCurrentSource(), lineNumber);
    }

    /** Removes the top element of the stack trace */
    public void clearStackTrace()
    {
       java.util.List<Object> strace = new java.util.LinkedList<Object>();
       getScriptEnvironment().getEnvironment().put("%strace%", strace);
    }

    /** Returns the last stack trace.  Each element of the list is a ScriptInstance.SleepStackElement object.  
        First element is the top of the trace, last element is the origin of the trace.  This function also
        clears the stack trace. */
    public java.util.List<Object> getStackTrace()
    {
       java.util.List<Object> strace = (java.util.List<Object>)getScriptEnvironment().getEnvironment().get("%strace%");
       clearStackTrace(); /* clear the old stack trace */
       if (strace == null)
       {
          strace = new java.util.LinkedList<Object>();
       }
       return strace;
    }

    /** A container for a profile statistic about a sleep function */
    [Serializable]
    public class ProfilerStatistic : java.lang.Comparable<Object>, java.io.Serializable
    {
        /** the name of the function call */
        public String functionName;

        /** the total number of ticks consumed by this function call */
        public long ticks = 0;

        /** the total number of times this function has been called */
        public long calls = 0;

        /** used to compare this statistic to other statistics for the sake of sorting */
        public int compareTo(Object o)
        {
           return (int)(((ProfilerStatistic)o).ticks - ticks);
        }

        /** returns a string in the form of (total time used in seconds)s (total calls made) @(line number) (function description) */ 
        public String toString()
        {
           return (ticks / 1000.0) + "s " + calls + " " + functionName;
        }
    }

    /** return the total number of ticks this script has spent processing */
    public long total()
    {
        Long total = (Long)getMetadata().get("%total%");
        return total == null ? 0L : total.longValue();
    }

    /** this function is used internally by the sleep interpreter to collect profiler statistics
        when DEBUG_TRACE_CALLS or DEBUG_TRACE_PROFILE_ONLY is enabled */
    public void collect(String function, int lineNo, long ticks)
    {
       Map    statistics = (Map)getMetadata().get("%statistics%");
       Long   total      = (Long)getMetadata().get("%total%");

       if (statistics == null) 
       {
          statistics = new HashMap();
          total      = new Long(0L);

          getMetadata().put("%statistics%", statistics);
          getMetadata().put("%total%", total);
       }

       ProfilerStatistic stats = (ProfilerStatistic)statistics.get(function);

       if (stats == null)
       {
          stats = new ProfilerStatistic();
          stats.functionName = function;

          statistics.put(function, stats);
       }

       /** updated individual statistics */
       stats.ticks += ticks;
       stats.calls ++;

       /** update global statistic */
       getMetadata().put("%total%", new Long(total.longValue() + ticks));
    }

    /** a quick way to check if we are profiling and not tracing the script steps */
    public bool isProfileOnly()
    {
       return (getDebugFlags() & DEBUG_TRACE_PROFILE_ONLY) == DEBUG_TRACE_PROFILE_ONLY;
    }

    /** Returns a sorted (in order of total ticks used) list of function call statistics for this
        script environment.  The list contains ScriptInstance.ProfileStatistic objects. 
        Note!!! For Sleep to provide profiler statistics, DEBUG_TRACE_CALLS or DEBUG_TRACE_PROFILE_ONLY must be enabled! */
    public java.util.List<Object> getProfilerStatistics()
    {
        java.util.Map<Object,Object> statistics = (java.util.Map<Object,Object>)getMetadata().get("%statistics%");

        if (statistics != null)
        {
           java.util.List<Object> values = new java.util.LinkedList<Object>(statistics.values());
           Collections.sort(values);

           return values;
        }
        else
        {
           return new java.util.LinkedList<Object>();
        }
    }

    /** retrieves script meta data for you to update */
    public java.util.Map<Object,Object> getMetadata()
    {
       Scalar container = getScriptVariables().getGlobalVariables().getScalar("__meta__");
       java.util.Map<Object,Object>    meta      = null;

       if (container == null)
       {
          meta = Collections.synchronizedMap(new java.util.HashMap<Object,Object>()); /* we do this because this metadata may be shared between multiple threads */
          getScriptVariables().getGlobalVariables().putScalar("__meta__", SleepUtils.getScalar((Object)meta));
       }
       else
       {
          meta = (java.util.Map<Object,Object>)container.objectValue();
       }

       return meta;
    }

    /** Dumps the profiler statistics to the specified stream */
    public void printProfileStatistics(java.io.OutputStream outJ)
    {
        java.io.PrintWriter pout = new java.io.PrintWriter(outJ, true);

        Iterator i = getProfilerStatistics().iterator();
        while (i.hasNext())
        {
           String temp = i.next().toString();
           pout.println(temp);
        }
    }

    /** Call this function if you're sharing a script environment with other script instances.  This will sanitize the current
        script environment to avoid leakage between closure scopes, coroutines, and continuations.  Call this after script loading / bridge installation and
        before you run any scripts. */
    public void makeSafe()
    {
        Hashtable oldEnv = environment.getEnvironment();
        Hashtable newEnv = new Hashtable(  (oldEnv.size() * 2) - 1  );

        /* reset the environment please */
        Iterator i = oldEnv.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry temp = (Map.Entry)i.next();
            if (temp.getKey().toString().charAt(0) == '&' && temp.getValue() is SleepClosure)
            {
               SleepClosure closure = new SleepClosure(this, ((SleepClosure)temp.getValue()).getRunnableCode());
               newEnv.put(temp.getKey(), closure);
            }
            else
            { 
               newEnv.put(temp.getKey(), temp.getValue());
            }
        }

        /* update the environment */
        environment.setEnvironment(newEnv);
    }

    /** Creates a forked script instance.  This does not work like fork in an operating system.  Variables are not copied, period.
        The idea is to create a fork that shares the same environment as this script instance. */
    public ScriptInstance fork()
    {
        ScriptInstance si = new ScriptInstance(variables.getGlobalVariables().createInternalVariableContainer(), environment.getEnvironment());
        si.makeSafe();

        /* set the other cool stuff pls */
        si.setName(getName());
        si.setDebugFlags(getDebugFlags());
        si.watchers = watchers;

        /* make sure things like profiler statistics and metadata are shared between threads. */
        si.getScriptVariables().getGlobalVariables().putScalar("__meta__", SleepUtils.getScalar((Object)getMetadata()));
 
        return si;
    }

    /** Executes this script, same as runScript() just here for Runnable compatability */
    public void run()
    {
        Scalar temp = runScript();

        if (parent != null)
        {
           parent.setToken(temp);
        }
    }

    protected sleep.bridges.io.IOObject parent = null;
    
    /** Sets up the parent of this script (in case it is being run via &amp;fork()).  When this script returns a value, the return value will be passed to the parent IOObject to allow retrieval with the &amp;wait function. */
    public void setParent(sleep.bridges.io.IOObject p)
    {
        parent = p;
    }

    /** Returns the compiled form of this script 
     *  @see #getRunnableScript
     */
    public Block getRunnableBlock()
    {
       return script.getRunnableCode();
    }

    /** Returns this toplevel script as a Sleep closure. */
    public SleepClosure getRunnableScript()
    {
       return script;
    }

    /** Calls a subroutine/built-in function using this script. */
    public Scalar callFunction(String funcName, java.util.Stack<Object> parameters)
    {
       Function myfunction = getScriptEnvironment().getFunction(funcName);

       if (myfunction == null)
       {
          return null;
       }

       Scalar evil = myfunction.evaluate(funcName, this, parameters);
       getScriptEnvironment().resetEnvironment();

       return evil;
    }

    /** Flag this script as unloaded */
    public void setUnloaded()
    {
       loaded = false;
    }

    /** Returns wether or not this script is loaded.  If it is unloaded it should be removed from data structures and
        its modifications to the environment should be ignored */
    public bool isLoaded()
    {
       return loaded;
    }

    /** Register a runtime warning watcher listener.  If an error occurs while the script is running these listeners will
        be notified */
    public void addWarningWatcher(RuntimeWarningWatcher w)
    {
       watchers.add(w);
    }

    /** Removes a runtime warning watcher listener */
    public void removeWarningWatcher(RuntimeWarningWatcher w)
    {
       watchers.remove(w);
    }

    /** Fire a runtime script warning */
    public void fireWarning(String message, int line)
    {
       fireWarning(message, line, false);
    }

    /** Fire a runtime script warning */
    public void fireWarning(String message, int line, bool isTrace)
    {
       if (debug != DEBUG_NONE && (!isTrace || (getDebugFlags() & DEBUG_TRACE_SUPPRESS) != DEBUG_TRACE_SUPPRESS))
       {
          ScriptWarning temp = new ScriptWarning(this, message, line, isTrace);

          Iterator i = watchers.iterator();
          while (i.hasNext())
          {
             ((RuntimeWarningWatcher)i.next()).processScriptWarning(temp);
          }
       }
    }
}



}