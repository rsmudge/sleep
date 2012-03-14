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

import java.io.Serializable;

import sleep.runtime.Scalar;
import sleep.engine.Block;
import sleep.engine.Step;

import sleep.bridges.BasicNumbers;
import sleep.bridges.BasicStrings;
import sleep.bridges.BasicUtilities;
import sleep.bridges.DefaultEnvironment;

import sleep.interfaces.*;

import sleep.parser.Parser;
import sleep.parser.ParserUtilities;

import java.util.*;
import java.util.Hashtable;
import java.util.Stack;

import sleep.runtime.SleepUtils;

import sleep.error.YourCodeSucksException;

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
public class ScriptEnvironment implements Serializable
{
    /** the script instance that this is the environment for */
    protected ScriptInstance  self;  

    /** the runtime data stack for this environment */
    protected Stack           environmentStack;

    /** the environment hashtable that contains all of the functions, predicates, operators, and "environment keywords" this 
        script has access to. */
    protected Hashtable       environment;

    /** Not recommended that you instantiate a script environment in this way */
    public ScriptEnvironment()
    {
       self        = null;
       environment = null;
       environmentStack = new Stack();
    }

    /** Instantiate a new script environment with the specified environment (can be shared), and the specified ScriptInstance */
    public ScriptEnvironment(Hashtable env, ScriptInstance myscript)
    {
       self        = myscript;
       environment = env;
       environmentStack = new Stack();
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

    public Environment getFunctionEnvironment(String env)
    {
       return (Environment)(getEnvironment().get(env));
    }

    public PredicateEnvironment getPredicateEnvironment(String env)
    {
       return (PredicateEnvironment)(getEnvironment().get(env));
    }

    public FilterEnvironment getFilterEnvironment(String env)
    {
       return (FilterEnvironment)(getEnvironment().get(env));
    }

    public Predicate getPredicate(String name)
    {
       return (Predicate)(getEnvironment().get(name));
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
    public Hashtable getEnvironment()
    {
       return environment;
    }

    /** Sets the environment Hashtable this script is to use.  Sharing an instance of this Hashtable allows scripts to share 
        common environment data like functions, subroutines, etc.   Also useful for bridge writers as their information can be
        stored in this hashtable as well */
    public void setEnvironment(Hashtable h)
    {
       environment = h;
    }
 
    /** returns the environment stack used for temporary calculations and such. */
    public Stack getEnvironmentStack()
    {
       return environmentStack;
    }

    public String toString()
    {
       StringBuffer temp = new StringBuffer();
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
    
    protected static class Context implements Serializable
    {
       public Block            block;
       public Step             last;       
       public ExceptionContext handler;
       public boolean          moreHandlers;
    }

    protected Stack    context      = new Stack();
    protected Stack    contextStack = new Stack();

    protected HashMap  metadata     = new HashMap();
    protected Stack    metaStack    = new Stack();

    public void loadContext(Stack _context, HashMap _metadata)
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
          Iterator i = context.iterator();
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

       Stack cstack = context;
       context      = new Stack();

       Iterator i = cstack.iterator();
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

    public Stack saveContext()
    {
       Stack cstack = context;

       context  = (Stack)(contextStack.pop());
       metadata = (HashMap)(metaStack.pop());

       return cstack;
    }
 
    //
    // ******** Exception Management **********
    //

    protected static class ExceptionContext implements Serializable
    {
       public Block  owner;
       public String varname;
       public Block  handler;
    }

    protected ExceptionContext currentHandler = null;
    protected Stack            exhandlers     = new Stack(); /* exception handlers */
    protected boolean          moreHandlers   = false;

    public boolean isExceptionHandlerInstalled()
    {
       return currentHandler != null || moreHandlers;
    }

    public boolean isResponsible(Block block)
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
    public static final int FLOW_CONTROL_NONE     = 0;

    /** request a return from the current function */
    public static final int FLOW_CONTROL_RETURN   = 1;

    /** request a break out of the current loop */
    public static final int FLOW_CONTROL_BREAK    = 2;

    /** adding a continue keyword as people keep demanding it */
    public static final int FLOW_CONTROL_CONTINUE = 4;

    /** adding a yield keyword */
    public static final int FLOW_CONTROL_YIELD    = 8;
   
    /** adding a throw keyword -- sleep is now useable :) */
    public static final int FLOW_CONTROL_THROW    = 16;

    /** a special case for debugs and such */ 
    public static final int FLOW_CONTROL_DEBUG    = 32;
 
    /** adding a callcc keyword */
    public static final int FLOW_CONTROL_CALLCC   = 8 | 64; 

    /** a special case, pass control flow to the return value (it better be a function!) */
    public static final int FLOW_CONTROL_PASS     = 128;

    protected String  debugString       = "";
    protected Scalar rv      = null;
    protected int    request = 0;

    public boolean isThrownValue()
    {
       return (request & FLOW_CONTROL_THROW) == FLOW_CONTROL_THROW;
    }

    public boolean isDebugInterrupt()
    {
       return (request & FLOW_CONTROL_DEBUG) == FLOW_CONTROL_DEBUG;
    }

    public boolean isYield()
    {
       return (request & FLOW_CONTROL_YIELD) == FLOW_CONTROL_YIELD;
    }

    public boolean isCallCC()
    {
       return (request & FLOW_CONTROL_CALLCC) == FLOW_CONTROL_CALLCC;
    }

    public boolean isPassControl()
    {
       return (request & FLOW_CONTROL_PASS) == FLOW_CONTROL_PASS;
    }

    public Scalar getReturnValue()
    {
       return rv;
    }

    public boolean isReturn()
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
    protected Stack sources = new Stack(); 

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
    protected ArrayList frames = new ArrayList(10);
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

    public Stack getCurrentFrame()
    {
       return (Stack)frames.get(findex);    
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

    public boolean hasFrame() { return findex >= 0; }

    public void KillFrame()
    {
       getCurrentFrame().clear();
       findex--;
    }
    
    public void CreateFrame(Stack frame)
    {
       if (frame == null) 
       { 
          frame = new Stack(); 
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
          frames.add(new Stack());
       } 

       findex++;
    }

    /** evaluate a full blown statement... probably best to just load a script at this point */
    public Scalar evaluateStatement(String code) throws YourCodeSucksException
    {
       return SleepUtils.runCode(SleepUtils.ParseCode(code), this);
    }

    /** evaluates a predicate condition */
    public boolean evaluatePredicate(String code) throws YourCodeSucksException
    {
       code = "if (" + code + ") { return 1; } else { return $null; }";
       return (SleepUtils.runCode(SleepUtils.ParseCode(code), this).intValue() == 1);
    }

    /** evaluates an expression */
    public Scalar evaluateExpression(String code) throws YourCodeSucksException
    {
       code = "return (" + code + ");";
       return SleepUtils.runCode(SleepUtils.ParseCode(code), this);
    }

    /** evaluates the passed in code as if it was a sleep parsed literal */
    public Scalar evaluateParsedLiteral(String code) throws YourCodeSucksException
    {
       code = "return \"" + code + "\";";
       return SleepUtils.runCode(SleepUtils.ParseCode(code), this);
    }
}
