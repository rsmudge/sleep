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
using System;
using java = biz.ritter.javapi;

using  sleep.engine;
using  sleep.runtime;
using  sleep.interfaces;
using  sleep.engine.types;
using  sleep.bridges;

namespace sleep.engine{


/** This class encapsulates a function call request.  Sleep has too many reasons, places, and ways to call functions.
 *  This class helps to avoid duplicate code and manage the complexity of Sleep's myriad of profiling, tracing, and error reporting
 *  options. 
 * 
 *  This functionality is encapsulated (along with necessary setup/teardown that you don't want to touch) within 
 *  {@linkplain sleep.runtime.SleepUtils#runCode(sleep.engine.Block, sleep.runtime.ScriptEnvironment) SleepUtils.runCode()}.
 *
 *  @see sleep.runtime.SleepUtils
 */
public abstract class CallRequest
{
   protected ScriptEnvironment environment;
   protected int               lineNumber;

   /** initialize a new call request */
   public CallRequest(ScriptEnvironment e, int lineNo)
   {
      environment = e;
      lineNumber  = lineNo;
   }

   /** returns the script environment... pHEAR */
   protected ScriptEnvironment getScriptEnvironment()
   {
      return environment;      
   }

   /** returns the line number this function call is occuring from */
   public int getLineNumber()
   {
      return lineNumber;
   }

   /** return the name of the function (for use in profiler statistics) */
   public abstract String getFunctionName();

   /** return the description of this current stack frame in the event of an exception */
   public abstract String getFrameDescription();

   /** execute the function call contained here */
   protected abstract Scalar execute();

   /** return a string view of this function call for trace messages; arguments are captured as comma separated descriptions of all args */
   public abstract String formatCall(String args);

   /** return true if debug trace is enabled.  override this to add/change criteria for trace activiation */
   public bool isDebug()
   {
      return (getScriptEnvironment().getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_TRACE_CALLS) == ScriptInstance.DEBUG_TRACE_CALLS;
   }

   /** actually execute the function call */
   public void CallFunction()
   {
      Scalar temp = null;
      ScriptEnvironment e = getScriptEnvironment();
      int mark = getScriptEnvironment().markFrame();

      if (isDebug() && getLineNumber() != java.lang.Integer.MIN_VALUE)
      {
         if (e.getScriptInstance().isProfileOnly())
         {
             try
             {
                long total = e.getScriptInstance().total();
                long stat  = java.lang.SystemJ.currentTimeMillis();
                temp       = execute();
                stat       = (java.lang.SystemJ.currentTimeMillis() - stat) - (e.getScriptInstance().total() - total);
                e.getScriptInstance().collect(getFunctionName(), getLineNumber(), stat);
             }
             catch (java.lang.RuntimeException rex)
             {
                if (rex.getCause() == null || ! (  
                   (rex.getCause() is java.lang.reflect.InvocationTargetException)))
                   //(java.lang.reflect.InvocationTargetException.class).isInstance(rex.getCause())  ))
                {
                   /* swallow invocation target exceptions please */

                   e.cleanFrame(mark);
                   e.KillFrame();
                   throw(rex);
                }
             }
         }
         else
         {
             String args = SleepUtils.describe(e.getCurrentFrame());

             try
             {
                long total = e.getScriptInstance().total();
                long stat = java.lang.SystemJ.currentTimeMillis();
                temp = execute();
                stat       = (java.lang.SystemJ.currentTimeMillis() - stat) - (e.getScriptInstance().total() - total);
                e.getScriptInstance().collect(getFunctionName(), getLineNumber(), stat);

                if (e.isThrownValue())
                {
                   e.getScriptInstance().fireWarning(formatCall(args) + " - FAILED!", getLineNumber(), true);
                }
                else if (e.isPassControl())
                {
                   e.getScriptInstance().fireWarning(formatCall(args) + " -goto- " + SleepUtils.describe(temp), getLineNumber(), true);
                }
                else if (SleepUtils.isEmptyScalar(temp))
                {
                   e.getScriptInstance().fireWarning(formatCall(args), getLineNumber(), true);
                }
                else
                {
                   e.getScriptInstance().fireWarning(formatCall(args) + " = " + SleepUtils.describe(temp), getLineNumber(), true);
                }
             }
             catch (java.lang.RuntimeException rex)
             {
                e.getScriptInstance().fireWarning(formatCall(args) + " - FAILED!", getLineNumber(), true);

                if (rex.getCause() == null || ! (  
                   (rex.getCause() is typeof(java.lang.reflect.InvocationTargetException))))
                   //(java.lang.reflect.InvocationTargetException.class).isInstance(rex.getCause())  ))
                {
                   /* swallow invocation target exceptions please */

                   e.cleanFrame(mark);
                   e.KillFrame();
                   throw(rex);
                }
             }
         }
      }
      else
      {
         try
         {
             temp = execute();
         }
         catch (java.lang.RuntimeException rex)
         {
             if (rex.getCause() == null || ! (  
                (rex.getCause() is typeof(java.lang.reflect.InvocationTargetException))))
                //(java.lang.reflect.InvocationTargetException.class).isInstance(rex.getCause())  ))
             {
                 /* swallow invocation target exceptions please */

                e.cleanFrame(mark);
                e.KillFrame();
                throw(rex);
             }
         }
      }

      if (e.isThrownValue())
      {
         e.getScriptInstance().recordStackFrame(getFrameDescription(), getLineNumber());
      }

      if (temp == null)
        temp = SleepUtils.getEmptyScalar();

      e.cleanFrame(mark);
      e.FrameResult(temp);

      /* if you're digging here then you've discovered my dirty little secret.  My continuation's continue to possess Java stack frames until
         something decides to return.  Moving this check into Block.java overcomes this limitation except it makes it so continuations don't work
         in code invoked outside of a Call instruction enclosed within Block.java.  If this is an issue email me and I'll look at better ways to
         eliminate this problem. */

      if (e.isPassControl())
      {
         Scalar callme = temp;

         e.pushSource(((SleepClosure)callme.objectValue()).getAndRemoveMetadata("sourceFile", "<unknown>") + "");
         int lno = ( (java.lang.Integer)(  ((SleepClosure)callme.objectValue()).getAndRemoveMetadata("sourceLine", new Integer(-1))  ) ).intValue();

         if (e.markFrame() >= 0)
         {
            Object check = e.getCurrentFrame().pop(); /* get rid of the function that we're going to callcc */

            if (check != temp)
            {
               e.getScriptInstance().fireWarning("bad callcc stack: " + SleepUtils.describe((Scalar)check) + " expected " + SleepUtils.describe(temp), lno);
            }
         }

         e.flagReturn(null, ScriptEnvironment.FLOW_CONTROL_NONE);

         e.CreateFrame(); /* create a frame because the function call will destroy it */

         /** pass the continuation as the first argument to the callcc'd closure */
         e.getCurrentFrame().push(((SleepClosure)callme.objectValue()).getAndRemoveMetadata("continuation", null));

         CallRequest.ClosureCallRequest request = new CallRequest.ClosureCallRequest(environment, lno, callme, "CALLCC");
         request.CallFunction();

         e.popSource();
      }
   }

   /** execute a closure with all of the trimmings. */
   public class ClosureCallRequest : CallRequest
   {
      protected String name;
      protected Scalar scalar;

      public ClosureCallRequest(ScriptEnvironment e, int lineNo, Scalar _scalar, String _name)
      :
         base(e, lineNo){
         scalar = _scalar;
         name   = _name;
      }

      public override String getFunctionName()
      {
         return ((SleepClosure)scalar.objectValue()).toStringGeneric();
      }

      public override String getFrameDescription()
      {
         return scalar.toString();
      }

      public override String formatCall(String args)
      {
         java.lang.StringBuffer buffer = new java.lang.StringBuffer("[" + SleepUtils.describe(scalar));

         if (name != null && name.length() > 0)
         {
            buffer.append(" " + name);
         }

         if (args.length() > 0)
         {
            buffer.append(": " + args);
         }

         buffer.append("]");

         return buffer.toString();
      }

      protected override Scalar execute()
      {
         Function func = SleepUtils.getFunctionFromScalar(scalar, getScriptEnvironment().getScriptInstance());

         Scalar result;
         result = func.evaluate(name, getScriptEnvironment().getScriptInstance(), getScriptEnvironment().getCurrentFrame());
         getScriptEnvironment().clearReturn();
         return result;
      }
   }

   /** execute a function with all of the debug, trace, etc.. support */
   public class FunctionCallRequest : CallRequest
   {
      protected String function;
      protected Function callme;

      public FunctionCallRequest(ScriptEnvironment e, int lineNo, String functionName, Function f)
      :
         base(e, lineNo){
         function = functionName;
         callme   = f;
      }

      public override String getFunctionName()
      {
         return function;
      }

      public override String getFrameDescription()    
      {
         return function + "()";
      }

      public override String formatCall(String args) 
      {
         return function + "(" + args + ")";
      }

      public bool isDebug()
      {
         return base.isDebug() && !function.equals("&@") && !function.equals("&%") && !function.equals("&warn");
      }

      protected override Scalar execute()
      {
         Scalar temp = callme.evaluate(function, getScriptEnvironment().getScriptInstance(), getScriptEnvironment().getCurrentFrame());
         getScriptEnvironment().clearReturn();
         return temp;
      }
   }

   /** execute a block of code inline with all the profiling, tracing, and other support */
   public class InlineCallRequest : CallRequest
   {
      protected String function;
      protected Block  inline;

      public InlineCallRequest(ScriptEnvironment e, int lineNo, String functionName, Block i)
      
         : base(e, lineNo){
         function = functionName;
         inline   = i;
      }

      public override String getFunctionName()
      {
         return "<inline> " + function;
      }

      public override String getFrameDescription()    
      {
         return "<inline> " + function + "()";
      }

      public override String formatCall(String args) 
      {
         return "<inline> " + function + "(" + args + ")";
      }

      protected override Scalar execute()
      {
         ScriptVariables vars = getScriptEnvironment().getScriptVariables();
         lock (vars)
         {
            Variable localLevel = vars.getLocalVariables();
            Scalar   oldargs    = localLevel.getScalar("@_");     /* save the current local variables */

            int targs = sleep.bridges.BridgeUtilities.initLocalScope(vars, localLevel, getScriptEnvironment().getCurrentFrame());
            Scalar eval = inline.evaluate(getScriptEnvironment());
    
            /* restore the argument variables */
            if (oldargs != null && oldargs.getArray() != null)
            {
               localLevel.putScalar("@_", oldargs);
               if (targs > 0)
               {
                  java.util.Iterator<Object> i = oldargs.getArray().scalarIterator();
                  int      count = 1;
                  while (i.hasNext() && count <= targs)
                  {
                     Scalar temp = (Scalar)i.next();
                     localLevel.putScalar("$" + count, temp);
                     count++;
                  }
               }
            }
            return eval;
         }
      }
   }
}
}