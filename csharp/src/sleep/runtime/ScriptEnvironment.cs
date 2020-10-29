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

using  sleep.engine;
using  sleep.bridges;
using  sleep.interfaces;
using  sleep.parser;
using  sleep.runtime;
using  sleep.error;

namespace sleep.runtime{
/**
 * <p>This class contains methods for accessing the data stack, return value of a function, and the environment hashtable 
 * for a script.  In sleep each ScriptInstance has a ScriptEnvironment object associated with it.  Most of the functions in 
 * this class are used internally by sleep.</p>
 *
 * <p>For the developers purposes, this class is your gateway into the runtime environment of a script.</p>
 * 
 * <p>If you use the evaluate methods to evaluate a snippet of code, they will be evaluated as if they were part of the 
 * script file that this ScriptEnvironment represents.</p>
 * 
 * <p>The Hashtable environment contains references for all of the loaded bridges this script has access to.  Every 
 * function, predicate, and operator is specified in the environment hashtable.  To force scripts to share this information 
 * use setEnvironment(Hashtable) and pass the same instance of Hashtable that other scripts are using.</p>
 *
 * <p>This class is instantiated by sleep.runtime.ScriptInstance.</p>
 *
 * @see sleep.runtime.ScriptLoader
 * @see sleep.runtime.ScriptInstance
 */
 [Serializable]
public class ScriptEnvironment : java.io.Serializable
{
    /** the script instance that this is the environment for */
    protected ScriptInstance  self;  

    /** the runtime data stack for this environment */
    protected java.util.Stack<Object>           environmentStack;

    /** the environment hashtable that contains all of the functions, predicates, operators, and "environment keywords" this 
        script has access to. */
    protected java.util.Hashtable<Object,Object>       environment;

    /** Not recommended that you instantiate a script environment in this way */
    public ScriptEnvironment()
    {
       self        = null;
       environment = null;
       environmentStack = new java.util.Stack<Object>();
    }

    /** Instantiate a new script environment with the specified environment (can be shared), and the specified ScriptInstance */
    public ScriptEnvironment(java.util.Hashtable<Object,Object> env, ScriptInstance myscript)
    {
       self        = myscript;
       environment = env;
       environmentStack = new java.util.Stack<Object>();
    }

    /** returns a reference to the script associated with this environment */
    public ScriptInstance getScriptInstance()
    {
       return self;
    }

    /** stored error message... */
    protected Object errorMessage = null;

    /** A utility for bridge writers to flag an error.  flags an error that script writers can then check for with checkError().  
        Currently used by the IO bridge openf, exec, and connect functions.  Major errors should bubble up as exceptions.  Small 
        stuff like being unable to open a certain file should be flagged this way. */
    public void flagError(Object message)
    {
       errorMessage = message;
        
       if ((getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_SHOW_WARNINGS) == ScriptInstance.DEBUG_SHOW_WARNINGS)
       {
          if ((getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_THROW_WARNINGS) == ScriptInstance.DEBUG_THROW_WARNINGS)
          {
             flagReturn(checkError(), FLOW_CONTROL_THROW);
          }
          else
          {
             showDebugMessage("checkError(): " + message);
          }
       }
    }

    /** once an error is checked using this function, it is cleared, the orignal error message is returned as well */
    public Scalar checkError()
    {
       Scalar temp  = SleepUtils.getScalar(errorMessage);
       errorMessage = null;
       return temp;
    }

    /** returns the variable manager for this script */
    public ScriptVariables getScriptVariables()
    {
       return getScriptInstance().getScriptVariables();
    }

    /** returns a scalar from this scripts environment */
    public Scalar getScalar(String key)
    {
       return getScriptVariables().getScalar(key, getScriptInstance());
    }

    /** puts a scalar into this scripts environment (global scope) */
    public void putScalar(String key, Scalar value)
    {
       getScriptVariables().putScalar(key, value);
    }

    public Block getBlock(String name)
    {
       return (Block)(getEnvironment().get("^" + name));
    }   
 
    public Function getFunction(String func)
    {
       return (Function)(getEnvironment().get(func));
    }

    public sleep.interfaces.Environment getFunctionEnvironment(String env)
    {
       return (sleep.interfaces.Environment)(getEnvironment().get(env));
    }

    public PredicateEnvironment getPredicateEnvironment(String env)
    {
       return (PredicateEnvironment)(getEnvironment().get(env));
    }

    public FilterEnvironment getFilterEnvironment(String env)
    {
       return (FilterEnvironment)(getEnvironment().get(env));
    }

    public sleep.interfaces.Predicate getPredicate(String name)
    {
       return (sleep.interfaces.Predicate)(getEnvironment().get(name));
    }

    public Operator getOperator(String oper)
    {
       return (Operator)(getEnvironment().get(oper));
    }

    /**
     * Returns the environment for this script.
     * The environment has the following formats for keys:
     * &amp;[keyname] - a sleep function
     * -[keyname] - assumed to be a unary predicate
     * [keyname]  - assumed to be an environment binding, predicate, or operator
     */
    public java.util.Hashtable<Object,Object> getEnvironment()
    {
       return environment;
    }

    /** Sets the environment Hashtable this script is to use.  Sharing an instance of this Hashtable allows scripts to share 
        common environment data like functions, subroutines, etc.   Also useful for bridge writers as their information can be
        stored in this hashtable as well */
    public void setEnvironment(java.util.Hashtable<Object,Object> h)
    {
       environment = h;
    }
 
    /** returns the environment stack used for temporary calculations and such. */
    public java.util.Stack<Object> getEnvironmentStack()
    {
       return environmentStack;
    }

    public String toString()
    {
       java.lang.StringBuffer temp = new java.lang.StringBuffer();
       temp.append("ScriptInstance -- " + getScriptInstance());
       temp.append("Misc Environment:\n");
       temp.append(getEnvironment().toString()); 
       temp.append("\nEnvironment Stack:\n");
       temp.append(getEnvironmentStack().toString());
       temp.append("Return Stuff: " + rv); 

       return temp.toString();
    }

    //
    // ******** Context Management **********
    //
    
   [Serializable]
    protected class Context : java.io.Serializable
    {
       public Block            block;
       public Step             last;       
       public ExceptionContext handler;
       public bool          moreHandlers;
    }

    protected java.util.Stack<Object>    context      = new java.util.Stack<Object>();
    protected java.util.Stack<Object>    contextStack = new java.util.Stack<Object>();

    protected java.util.HashMap<Object,Object>  metadata     = new java.util.HashMap<Object,Object>();
    protected java.util.Stack<Object>    metaStack    = new java.util.Stack<Object>();

    public void loadContext(java.util.Stack<Object> _context, java.util.HashMap<Object,Object> _metadata)
    {
       contextStack.push(context);
       metaStack.push(metadata); 

       context  = _context;
       metadata = _metadata;
    }

    /** Use this function to save some meta data for this particular closure context, passing null for value will
        remove the key from the metadata for this context.
       
        Note: context metadata is *not* serialized when the closure is serialized.
    */
    public void setContextMetadata(Object key, Object value)
    {
       if (value == null) 
       {
          metadata.remove(key);
       }
       else
       {
          metadata.put(key, value);
       }
    }

    /** Returns the data associated with the particular key for this context. */
    public Object getContextMetadata(Object key)
    {
       return metadata.get(key);
    }

    /** Returns the data associated with the particular key for this context. If the key value is null then the specified default_value is returned */
    public Object getContextMetadata(Object key, Object default_value)
    {
       Object value = metadata.get(key);

       if (value == null)
       {
          return default_value;
       }
 
       return metadata.get(key);
    }

    public void addToContext(Block b, Step s)
    {
       Context temp = new Context();
       temp.block = b;
       temp.last  = s;

       if (isResponsible(b))
       {
          temp.handler = popExceptionContext();
          java.util.Iterator<Object> i = context.iterator();
          while (i.hasNext())
          {  /* semi inefficient but there should be so few handlers per context this shouldn't be much of an issue */
             Context c = (Context)i.next();
             c.moreHandlers = true;
          }
       }
       else
       {
          temp.moreHandlers = moreHandlers; /* if a context is already executing then it will know better than we do
                                               wether there are more handlers in the current context or not */
       }

       context.add(temp);
    }

    public Scalar evaluateOldContext()
    {
       Scalar rv = SleepUtils.getEmptyScalar();

       java.util.Stack<Object> cstack = context;
       context      = new java.util.Stack<Object>();

       java.util.Iterator<Object> i = cstack.iterator();
       while (i.hasNext())
       {
          Context temp = (Context)i.next();

          if (temp.handler != null)
              installExceptionHandler(temp.handler);

          moreHandlers = temp.moreHandlers;

          rv = temp.block.evaluate(this, temp.last);

          if (isReturn() && isYield())
          {
             while (i.hasNext())
             {
                context.add(i.next()); /* adding the remaining context so it doesn't get lost */
             } 
          }
       }

       moreHandlers = false;
       return rv;
    }

    public java.util.Stack<Object> saveContext()
    {
       java.util.Stack<Object> cstack = context;

       context  = (java.util.Stack<Object>)(contextStack.pop());
       metadata = (java.util.HashMap<Object,Object>)(metaStack.pop());

       return cstack;
    }
 
    //
    // ******** Exception Management **********
    //
[Serializable]
    public class ExceptionContext : java.io.Serializable
    {
       public Block  owner;
       public String varname;
       public Block  handler;
    }

    protected ExceptionContext currentHandler = null;
    protected java.util.Stack<Object>            exhandlers     = new java.util.Stack<Object>(); /* exception handlers */
    protected bool          moreHandlers   = false;

    public bool isExceptionHandlerInstalled()
    {
       return currentHandler != null || moreHandlers;
    }

    public bool isResponsible(Block block)
    {
       return currentHandler != null && currentHandler.owner == block;
    }

    public void installExceptionHandler(ExceptionContext exc)
    {
       if (currentHandler != null)
          exhandlers.push(currentHandler);

       currentHandler = exc;
    }

    public void installExceptionHandler(Block owner, Block handler, String varname)
    {
       ExceptionContext c = new ExceptionContext();
       c.owner   = owner;
       c.handler = handler;
       c.varname = varname;

       installExceptionHandler(c);
    }

    /** if there is no handler, we'll just get the message which will clear the thrown message as well */
    public Scalar getExceptionMessage()
    {
       request     &= ~FLOW_CONTROL_THROW;       
       Scalar temp  = rv;
       rv           = null;
       return temp;
    }

    /** preps and returns the current exception handler... */
    public Block getExceptionHandler()
    {
       request     &= ~FLOW_CONTROL_THROW;       
       Block  doit  = currentHandler.handler;

       Scalar temp  = getScriptVariables().getScalar(currentHandler.varname, getScriptInstance());
       if (temp != null)
       {
          temp.setValue(rv);
       }
       else
       {
          putScalar(currentHandler.varname, rv);
       }
       rv           = null;
       return doit;
    }

    public ExceptionContext popExceptionContext()
    {
       ExceptionContext old = currentHandler;

       if (exhandlers.isEmpty())
       {
          currentHandler = null;
       }
       else
       {
          currentHandler = (ExceptionContext)exhandlers.pop();
       }

       return old;
    }

    //
    // ******** Flow Control **********
    //

    /** currently no flow control change has been requested */
    public static readonly int FLOW_CONTROL_NONE     = 0;

    /** request a return from the current function */
    public static readonly int FLOW_CONTROL_RETURN   = 1;

    /** request a break out of the current loop */
    public static readonly int FLOW_CONTROL_BREAK    = 2;

    /** adding a continue keyword as people keep demanding it */
    public static readonly int FLOW_CONTROL_CONTINUE = 4;

    /** adding a yield keyword */
    public static readonly int FLOW_CONTROL_YIELD    = 8;
   
    /** adding a throw keyword -- sleep is now useable :) */
    public static readonly int FLOW_CONTROL_THROW    = 16;

    /** a special case for debugs and such */ 
    public static readonly int FLOW_CONTROL_DEBUG    = 32;
 
    /** adding a callcc keyword */
    public static readonly int FLOW_CONTROL_CALLCC   = 8 | 64; 

    /** a special case, pass control flow to the return value (it better be a function!) */
    public static readonly int FLOW_CONTROL_PASS     = 128;

    protected String  debugString       = "";
    protected Scalar rv      = null;
    protected int    request = 0;

    public bool isThrownValue()
    {
       return (request & FLOW_CONTROL_THROW) == FLOW_CONTROL_THROW;
    }

    public bool isDebugInterrupt()
    {
       return (request & FLOW_CONTROL_DEBUG) == FLOW_CONTROL_DEBUG;
    }

    public bool isYield()
    {
       return (request & FLOW_CONTROL_YIELD) == FLOW_CONTROL_YIELD;
    }

    public bool isCallCC()
    {
       return (request & FLOW_CONTROL_CALLCC) == FLOW_CONTROL_CALLCC;
    }

    public bool isPassControl()
    {
       return (request & FLOW_CONTROL_PASS) == FLOW_CONTROL_PASS;
    }

    public Scalar getReturnValue()
    {
       return rv;
    }

    public bool isReturn()
    {
       return request != FLOW_CONTROL_NONE;
    }

    public int getFlowControlRequest()
    {
       return request;
    }

    public String getDebugString()
    {
       request &= ~FLOW_CONTROL_DEBUG;
       return debugString;
    }

    /** fires this debug message via a runtime warning complete with line number of current step */
    public void showDebugMessage(String message)
    {
       request |= FLOW_CONTROL_DEBUG;
       debugString = message;
    }
  
    /** flags a return value for this script environment */
    public void flagReturn(Scalar value, int type_of_flow)
    {
       if (value == null) { value = SleepUtils.getEmptyScalar(); }
       rv      = value;
       request = type_of_flow;
    }

    /** Resets the script environment to include clearing the return of all flags (including thrown exceptions) */
    public void resetEnvironment()
    {
       errorMessage = null;
       request = FLOW_CONTROL_NONE;
       rv      = null;
       getScriptInstance().clearStackTrace(); /* no one else is going to use it, right?!? */
    }

    /** Clears the return value from the last executed function. */
    public void clearReturn()
    {
       request = FLOW_CONTROL_NONE | (request & (FLOW_CONTROL_THROW | FLOW_CONTROL_DEBUG | FLOW_CONTROL_PASS));

       if (!isThrownValue() && !isPassControl())
           rv      = null;
    }

    /** how many stacks does this damned class include? */
    protected java.util.Stack<Object> sources = new java.util.Stack<Object>(); 

    /** push source information onto the source stack */
    public void pushSource(String s) 
    {
       sources.push(s);
    }

    /** obtain the filename of the current source of execution */
    public String getCurrentSource()
    {
       if (!sources.isEmpty())
          return sources.peek() + "";

       return "unknown";
    }
 
    /** remove the latest source information from the source stack */
    public void popSource()
    {
       sources.pop();
    }

    //
    // stuff related to frame management
    //
    protected java.util.ArrayList<Object> frames = new java.util.ArrayList<Object>(10);
    protected int       findex = -1;

    /** markFrame and cleanFrame are used to keep the sleep stack in good order after certain error conditions */
    public int markFrame()
    {
        return findex;
    }

    /** markFrame and cleanFrame are used to keep the sleep stack in good order after certain error conditions */
    public void cleanFrame(int mark)
    {
        while (findex > mark)
        {
           KillFrame();
        }
    } 

    public java.util.Stack<Object> getCurrentFrame()
    {
       return (java.util.Stack<Object>)frames.get(findex);    
    }

    /** kills the current frame and if there is a parent frame pushes the specified value on to it */
    public void FrameResult(Scalar value)
    {
       KillFrame();
       if (findex >= 0)
       {
          getCurrentFrame().push(value);
       }
    }

    public bool hasFrame() { return findex >= 0; }

    public void KillFrame()
    {
       getCurrentFrame().clear();
       findex--;
    }
    
    public void CreateFrame(java.util.Stack<Object> frame)
    {
       if (frame == null) 
       { 
          frame = new java.util.Stack<Object>(); 
       }

       if ((findex + 1) >= frames.size())
       {
          frames.add(frame);
       } 
       else
       {
          frames.set(findex + 1, frame);
       }

       findex++;
    }

    public void CreateFrame()
    {
       if ((findex + 1) >= frames.size())
       {
          frames.add(new java.util.Stack<Object>());
       } 

       findex++;
    }

    /** evaluate a full blown statement... probably best to just load a script at this point */
    public Scalar evaluateStatement(String code) //throws YourCodeSucksException
    {
       return SleepUtils.runCode(SleepUtils.ParseCode(code), this);
    }

    /** evaluates a predicate condition */
    public bool evaluatePredicate(String code) //throws YourCodeSucksException
    {
       code = "if (" + code + ") { return 1; } else { return $null; }";
       return (SleepUtils.runCode(SleepUtils.ParseCode(code), this).intValue() == 1);
    }

    /** evaluates an expression */
    public Scalar evaluateExpression(String code) //throws YourCodeSucksException
    {
       code = "return (" + code + ");";
       return SleepUtils.runCode(SleepUtils.ParseCode(code), this);
    }

    /** evaluates the passed in code as if it was a sleep parsed literal */
    public Scalar evaluateParsedLiteral(String code) //throws YourCodeSucksException
    {
       code = "return \"" + code + "\";";
       return SleepUtils.runCode(SleepUtils.ParseCode(code), this);
    }
}
}