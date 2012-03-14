/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
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
package sleep.runtime;

import sleep.runtime.Scalar;
import sleep.engine.Block;
import sleep.engine.CallRequest;

import sleep.bridges.BasicNumbers;
import sleep.bridges.BasicStrings;
import sleep.bridges.BasicIO;
import sleep.bridges.BasicUtilities;
import sleep.bridges.DefaultEnvironment;
import sleep.bridges.DefaultVariable;
import sleep.bridges.RegexBridge;
import sleep.bridges.SleepClosure;

import sleep.interfaces.*;
import sleep.error.*;

import sleep.parser.Parser;
import sleep.parser.ParserUtilities;

import java.util.*;
import java.io.*;

/** Every piece of information related to a loaded script.  This includes the scripts runtime environment, code in compiled 
  * form, variable information, and listeners for runtime issues.
  */
public class ScriptInstance implements Serializable, Runnable
{
    /** the name of this script */
    protected String  name   = "Script";

    /** true by default, indicates wether or not the script is loaded.  Once unloaded this variable must be flagged to false so
        the bridges know data related to this script is stale */
    protected boolean loaded; 

    /** A list of listeners watching for a runtime error */
    protected LinkedList watchers = new LinkedList(); 

    /** The script environment which contains all of the runtime info for a script */
    protected ScriptEnvironment environment;

    /** The script variables which contains all of the variable information for a script */
    protected ScriptVariables   variables;

    /** The compiled sleep code for this script, the ScriptLoader will set this value upon loading a script. */
    protected SleepClosure      script;

    /** debug should be absolutely quiet, never fire any runtime warnings */
    public static final int DEBUG_NONE          = 0;

    /** fire runtime warnings for all critical flow interrupting errors */
    public static final int DEBUG_SHOW_ERRORS   = 1;

    /** fire runtime warnings for anything flagged for retrieval with checkError() */
    public static final int DEBUG_SHOW_WARNINGS = 2;

    /** fire runtime warning whenever an undeclared variable is fired */
    public static final int DEBUG_REQUIRE_STRICT = 4;

    /** fire a runtime warning describing each function call */
    public static final int DEBUG_TRACE_CALLS    = 8;

    /** forces function call tracing to occur (for the sake of profiling a script) but supresses
        all runtime warnings as a result of the tracing */
    public static final int DEBUG_TRACE_PROFILE_ONLY = 8 | 16;

    /** users shouldn't need to flag this, it is just a general method of saying we're suppressing
        trace messages... */
    protected static final int DEBUG_TRACE_SUPPRESS = 16;

    /** throw exceptions for anything flagged for retrieval with checkError() */
    public static final int DEBUG_THROW_WARNINGS = 2 | 32;

    /** fire a runtime warning describing each predicate decision made */
    public static final int DEBUG_TRACE_LOGIC = 64;

    /** trace the passage of tainted data */
    public static final int DEBUG_TRACE_TAINT = 128;

    /** track all of the flagged debug options for this script (set to DEBUG_SHOW_ERRORS by default) */
    protected int debug = DEBUG_SHOW_ERRORS;

    /** track the time this script was loaded */
    protected long loadTime = System.currentTimeMillis();

    /** list of source files associated with this script (to account for &include) */
    protected List sourceFiles = new LinkedList();

    /** associates the specified source file with this script */
    public void associateFile(File f)
    {
       if (f.exists())
       {
          sourceFiles.add(f);
       }
    }

    /** this script instance checks if (to the best of its knowledge) any of its source files have changed */
    public boolean hasChanged()
    {
       Iterator i = sourceFiles.iterator();
       while (i.hasNext())
       {
          File temp = (File)i.next();
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
    public ScriptInstance(Hashtable environmentToShare)
    {
        this((Variable)null, environmentToShare);
    }

    /** Constructs a script instance, if either of the parameters are null a default implementation will be used.
        By specifying the same shared Variable and Hashtable containers for all scripts, scripts can be made to
        share variables and environment information */
    public ScriptInstance(Variable varContainerToUse, Hashtable environmentToShare)
    {
        if (environmentToShare == null)
        {
           environmentToShare = new Hashtable();
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
    public static class SleepStackElement implements Serializable
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
    public File cwd()
    {
       if (!getMetadata().containsKey("__CWD__"))
       {
          chdir(null);
       }

       return (File)getMetadata().get("__CWD__");
    }

    /** sets the current working directory value for this script */
    public void chdir(File f)
    {
       if (f == null)
       {
           f = new File("");
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
       List strace = new LinkedList();
       getScriptEnvironment().getEnvironment().put("%strace%", strace);
    }

    /** Returns the last stack trace.  Each element of the list is a ScriptInstance.SleepStackElement object.  
        First element is the top of the trace, last element is the origin of the trace.  This function also
        clears the stack trace. */
    public List getStackTrace()
    {
       List strace = (List)getScriptEnvironment().getEnvironment().get("%strace%");
       clearStackTrace(); /* clear the old stack trace */
       if (strace == null)
       {
          strace = new LinkedList();
       }
       return strace;
    }

    /** A container for a profile statistic about a sleep function */
    public static class ProfilerStatistic implements Comparable, Serializable
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
    public boolean isProfileOnly()
    {
       return (getDebugFlags() & DEBUG_TRACE_PROFILE_ONLY) == DEBUG_TRACE_PROFILE_ONLY;
    }

    /** Returns a sorted (in order of total ticks used) list of function call statistics for this
        script environment.  The list contains ScriptInstance.ProfileStatistic objects. 
        Note!!! For Sleep to provide profiler statistics, DEBUG_TRACE_CALLS or DEBUG_TRACE_PROFILE_ONLY must be enabled! */
    public List getProfilerStatistics()
    {
        Map statistics = (Map)getMetadata().get("%statistics%");

        if (statistics != null)
        {
           List values = new LinkedList(statistics.values());
           Collections.sort(values);

           return values;
        }
        else
        {
           return new LinkedList();
        }
    }

    /** retrieves script meta data for you to update */
    public Map getMetadata()
    {
       Scalar container = getScriptVariables().getGlobalVariables().getScalar("__meta__");
       Map    meta      = null;

       if (container == null)
       {
          meta = Collections.synchronizedMap(new HashMap()); /* we do this because this metadata may be shared between multiple threads */
          getScriptVariables().getGlobalVariables().putScalar("__meta__", SleepUtils.getScalar((Object)meta));
       }
       else
       {
          meta = (Map)container.objectValue();
       }

       return meta;
    }

    /** Dumps the profiler statistics to the specified stream */
    public void printProfileStatistics(OutputStream out)
    {
        PrintWriter pout = new PrintWriter(out, true);

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
            if (temp.getKey().toString().charAt(0) == '&' && temp.getValue() instanceof SleepClosure)
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
    public Scalar callFunction(String funcName, Stack parameters)
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
    public boolean isLoaded()
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
    public void fireWarning(String message, int line, boolean isTrace)
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



