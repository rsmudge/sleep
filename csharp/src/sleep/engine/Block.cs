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

using  java.util;
using  java.io;

using  sleep.interfaces.Function;
using  sleep.runtime;
using  sleep.bridges.SleepClosure;

namespace sleep.engine{


/**
 * <p>A Block is the fundamental unit of parsed and ready to execute sleep code.</p>
 * 
 * <p>To execute a block of code:</p>
 * <pre>
 * ScriptInstance    script; // asume
 * Block             code;   // assume
 * 
 * ScriptEnvironment env   = script.getEnvironment();  
 * Scalar            value = SleepUtils.runCode(code, env);
 * </pre>
 * 
 * <p>The variable value would contain the return value after the block was executed.  It is recommended blocks only be run 
 * using {@linkplain sleep.runtime.SleepUtils#runCode(Block, ScriptEnvironment) SleepUtils.runCode()} as there is a little 
 * bit of synchronization and cleanup that has to be done prior to and after executing a block of code.</p>
 * 
 * @see sleep.runtime.Scalar
 * @see sleep.runtime.ScriptEnvironment
 * @see sleep.runtime.ScriptInstance
 */
 [Serializable]
public class Block : Serializable
{
    /** our first step in this block */
    protected Step  first;

    /** our last step in this block */
    protected Step  last;

    /** an identifier/tag/whatever identifying the source of this block (i.e. somescript.sl) */
    protected String source = "unknown";   

    public Block(String _src)
    {
       source = _src;
    }

    public String toString(String prefix)
    { 
       StringBuffer tempz = new StringBuffer();
       Step temp = first;
       while (temp != null)
       {
          tempz.append(temp.toString(prefix));
          temp = temp.next;
       }
       return tempz.toString();
    }

    /** Returns a string representation of the Abstract Syntax Tree (AST).  An AST represents how the sleep parser interpreted a script string */
    public String toString()
    {
       return toString("");
    }

    /** Returns the source identifier for this block */
    public String getSource()
    {
       return source;
    }

    /** Returns an approximated line number for the steps in this block object...  returns -1 if no code is in this block (unlikely) */
    public int getApproximateLineNumber()
    {
       if (first != null)
          return first.getLineNumber();

       return -1;
    }

    /** return the highest line number associated with this block */
    public int getHighLineNumber()
    {
       int high = 0;
 
       int m;
       Step temp = first;
       while (temp != null)
       {
          m = temp.getHighLineNumber();

          if (m > high)
              high = m;

          temp = temp.next;
       }

       return high;
    }

    /** return the lowest line number associated with this block */
    public int getLowLineNumber()
    {
       int low = Integer.MAX_VALUE;
 
       int m;
       Step temp = first;
       while (temp != null)
       {
          m = temp.getLowLineNumber();

          if (m < low)
              low = m;

          temp = temp.next;
       }

       return low;
    }

    /** Returns an approximate range of line numbers for the steps in this block object.  Useful for formatting error messages in script warnings and such. */
    public String getApproximateLineRange()
    {
       int low  = getLowLineNumber();
       int high = getHighLineNumber();

       if (low == high)
          return low + "";

       return low + "-" + high;
    }

    /** Returns a string representation of where in the source code this block originated from */
    public String getSourceLocation()
    {
       return (new File(source).getName()) + ":" + getApproximateLineRange();
    }

    public void add(Step n)
    {
       if (first == null)
       {
          first = n;
       }
       else
       {
          last.next = n;
       }

       last = n;
    }

    /** evaluates this block of code.  please note that if the block has a return statement and the method clearReturn() is not 
        called on the corresponding script environment chaos will ensue.  use SleepUtils.runCode() to safely execute a block of
        code.  don't call this method yourself.  okay? */
    public Scalar evaluate(ScriptEnvironment environment)
    {
        return evaluate(environment, first);
    }

    /** handle an exception raised by a script / java object */
    private void handleException(ScriptEnvironment environment)
    {
        if (environment.isResponsible(this))
        {
           Block doit = environment.getExceptionHandler();
           doit.evaluate(environment);
           environment.getScriptInstance().clearStackTrace();
        }
    }

    /** clean up the environment */
    private void cleanupEnvironment(ScriptEnvironment environment)
    {
        /* pop source information from the stack */
        environment.popSource();

        /* remove exception handlers associated with this block (we obviously returned within one */
        if (environment.isResponsible(this))
        {
           environment.popExceptionContext();
        }
    }

    /** evaluates this block of code.  please note that if the block has a return statement and the method clearReturn() is not 
        called on the corresponding script environment chaos will ensue.  use SleepUtils.runCode() to safely execute a block of
        code.  don't call this method yourself.  okay? */
    public Scalar evaluate(ScriptEnvironment environment, Step start)
    {
        if (environment.isReturn())
        {
           if (environment.isThrownValue())
           {
              environment.pushSource(source); /* may not be necessary, but then again, maybe they are... used for stack traces */
              handleException(environment);
              cleanupEnvironment(environment);
           }

           return environment.getReturnValue();
        }

        environment.pushSource(source);

        Step temp = start;
        while (temp != null)
        {
           try
           {
              temp.evaluate(environment);
           }
           catch (Exception ex)
           {
              if (ex is IllegalArgumentException)
              {
                 environment.getScriptInstance().fireWarning(ex.getMessage(), temp.getLineNumber());
              }
              else if (ex is IndexOutOfBoundsException)
              {
                 if (ex.getMessage() != null)
                 {
                    environment.getScriptInstance().fireWarning("attempted an invalid index: " + ex.getMessage(), temp.getLineNumber());
                 }
                 else
                 {
                    environment.getScriptInstance().fireWarning("attempted an invalid index", temp.getLineNumber());
                 }
              }
              else if (ex is ClassCastException)
              {
                 environment.getScriptInstance().fireWarning("attempted an invalid cast: " + ex.getMessage(), temp.getLineNumber());
              }
              else if (ex is NullPointerException)
              {
                 environment.getScriptInstance().fireWarning("null value error", temp.getLineNumber());
              }
              else if (ex is ConcurrentModificationException)
              {
                 if (ex.getMessage()  != null)
                 {
                    environment.getScriptInstance().fireWarning("unsafe data modification: " + ex.getMessage(), temp.getLineNumber());
                 }
                 else
                 {
                    environment.getScriptInstance().fireWarning("detected unsafe data modification", temp.getLineNumber());
                 }
              }    
              else if (ex is RuntimeException)
              {
                 if (ex.getMessage() == null)
                 {
                    environment.getScriptInstance().fireWarning("internal error - " + ex.getClass(), temp.getLineNumber());
//                    ex.printStackTrace(System.err);
                 }
                 else
                 {
                    environment.getScriptInstance().fireWarning(ex.getMessage(), temp.getLineNumber());
                 }
              }
              else
              {
                 environment.getScriptInstance().fireWarning(ex.toString(), temp.getLineNumber());
//                 ex.printStackTrace(System.err);
              }

              cleanupEnvironment(environment);
              return SleepUtils.getEmptyScalar();
           } 
           catch (Error th)
           {
              environment.getScriptInstance().fireWarning("critical internal error - " + th.toString(), temp.getLineNumber());
              cleanupEnvironment(environment);
              throw th;
           }

           while (environment.isReturn())
           {
              if (environment.isYield())
              {
                 if (temp is sleep.engine.atoms.Goto)
                 {
                    environment.addToContext(this, temp);
                 }
                 else
                 {
                    environment.addToContext(this, temp.next);
                 }
              }

              if (environment.isCallCC())
              {
                 environment.getCurrentFrame().push(source);
                 environment.getCurrentFrame().push(new Integer(temp.getLineNumber()));
              }

              if (environment.isThrownValue())
              {
                 if (!environment.isExceptionHandlerInstalled())
                 {
                    /* if no handler is installed we will fire a warning and then flag a return of $null so at least the
                       current function fails for not installing a handler */

                    if (!SleepUtils.isEmptyScalar(environment.getReturnValue()))
                    {
                       environment.getScriptInstance().fireWarning("Uncaught exception: " + environment.getExceptionMessage(), temp.getLineNumber());

                       /* the empty throw will cause the current script environment to essentially "exit" */
                       environment.flagReturn(null, ScriptEnvironment.FLOW_CONTROL_THROW);
                    }
                 }
                 else
                 {
                    if (!SleepUtils.isEmptyScalar(environment.getReturnValue())) /* an empty return value means we're exiting */
                    {
                       handleException(environment);
                    }
                 }

                 cleanupEnvironment(environment);
                 return environment.getReturnValue(); /* we do this because the exception will get cleared and after that
                                                         there may be a return value */
              }
              else if (environment.isDebugInterrupt())
              {
                 environment.getScriptInstance().fireWarning(environment.getDebugString(), temp.getLineNumber());
                 /** get debug string clears the debug interrupt! */
              }
              else
              {
                 cleanupEnvironment(environment);
                 return environment.getReturnValue();
              }
           }

           temp = temp.next;
        }

        cleanupEnvironment(environment);
        return SleepUtils.getEmptyScalar(); 
    }
}
}