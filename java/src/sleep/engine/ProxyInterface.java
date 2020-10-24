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
package sleep.engine;

import java.lang.reflect.*;
import java.util.*;

import sleep.runtime.*;

import sleep.engine.types.*;
import sleep.interfaces.Function;

import sleep.bridges.*;

/** This class is used to mock an instance of a class that implements a specified Java interface 
    using a Sleep function. */
public class ProxyInterface implements InvocationHandler
{
   protected ScriptInstance    script;
   protected Function          func;

   public ProxyInterface(Function _method, ScriptInstance _script)
   {
      func        = _method;
      script      = _script;
   }

   /** Returns the script associated with this proxy interface. */
   public ScriptInstance getOwner()
   {
      return script;
   }

   /** Returns a string description of this proxy interface */
   public String toString()
   {
      return func.toString();
   }

   /** Constructs a new instance of the specified class that uses the passed Sleep function to respond
       to all method calls on this instance. */
   public static Object BuildInterface(Class className, Function subroutine, ScriptInstance script)
   {
      return BuildInterface(new Class[] { className }, subroutine, script);
   } 

   /** Constructs a new instance of the specified class that uses the passed Sleep function to respond
       to all method calls on this instance. */
   public static Object BuildInterface(Class classes[], Function subroutine, ScriptInstance script)
   {
      InvocationHandler temp = new ProxyInterface(subroutine, script);
      return Proxy.newProxyInstance(classes[0].getClassLoader(), classes, temp);
   } 

   /** Constructs a new instance of the specified class that uses the passed block to respond
       to all method calls on this instance. */
   public static Object BuildInterface(Class className, Block block, ScriptInstance script)
   {
      return BuildInterface(className, new SleepClosure(script, block), script);
   } 

   /** Constructs a new instance of the specified class that uses the passed block to respond
       to all method calls on this instance. */
   public static Object BuildInterface(Class classes[], Block block, ScriptInstance script)
   {
      return BuildInterface(classes, new SleepClosure(script, block), script);
   } 

   /** This function invokes the contained Sleep closure with the specified arguments */
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      synchronized (script.getScriptVariables())
      {
         script.getScriptEnvironment().pushSource("<Java>");

         Stack temp = new Stack();

         boolean isTrace = (script.getDebugFlags() & ScriptInstance.DEBUG_TRACE_CALLS) == ScriptInstance.DEBUG_TRACE_CALLS;
         StringBuffer message = null;

         if (args != null)
         {
            for (int z = args.length - 1; z >= 0; z--)
            { 
               temp.push(ObjectUtilities.BuildScalar(true, args[z]));
            }
         }

         Scalar value;

         script.getScriptEnvironment().installExceptionHandler(null, null, null);

         if (isTrace)
         {
            if (!script.isProfileOnly())
            {
               message = new StringBuffer("[" + func + " " + method.getName());

               if (!temp.isEmpty())
                   message.append(": " + SleepUtils.describe(temp));

               message.append("]");
            }

            long stat = System.currentTimeMillis();
            value = func.evaluate(method.getName(), script, temp); 
            stat = System.currentTimeMillis() - stat;

            if (func.getClass() == SleepClosure.class)
            {
               script.collect(((SleepClosure)func).toStringGeneric(), -1, stat);
            }

            if (message != null)
            {
               if (script.getScriptEnvironment().isThrownValue()) 
                  message.append(" - FAILED!"); 
               else
                  message.append(" = " + SleepUtils.describe(value)); 

               script.fireWarning(message.toString(), -1, true);
            }
         }
         else
         {  
            value = func.evaluate(method.getName(), script, temp); 
         }
         script.getScriptEnvironment().popExceptionContext();
         script.getScriptEnvironment().clearReturn();
         script.getScriptEnvironment().popSource();
 
         if (script.getScriptEnvironment().isThrownValue())
         {
            script.recordStackFrame(func + " as " + method.toString(), "<Java>", -1);

            Object exvalue = (script.getScriptEnvironment().getExceptionMessage()).objectValue();
             
            if (exvalue instanceof Throwable)
            {
               throw (Throwable)exvalue;
            }
            else
            {
               throw new RuntimeException(exvalue.toString());
            }
         }        

         if (value != null)
            return ObjectUtilities.buildArgument(method.getReturnType(), value, script);

         return null;
      }
   }
}
	
