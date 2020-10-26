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
using  sleep.interfaces;
using  sleep.engine;
using  sleep.runtime;

using  java.lang.reflect;

namespace sleep.engine.atoms{

public class ObjectNew : Step
{
   protected Class name;

   public ObjectNew(Class _name)
   {
      name = _name;
   }

   public String toString()
   {
      return "[Object New]: "+name+"\n";
   }

   private static class ConstructorCallRequest : CallRequest
   {
      protected Constructor theConstructor;
      protected Class  name;

      public ConstructorCallRequest(ScriptEnvironment e, int lineNo, Constructor cont, Class _name)
      {
         super(e, lineNo);
         theConstructor = cont;
         name      = _name;
      }

      public String getFunctionName()
      {
         return name.toString();
      }

      public String getFrameDescription()
      {
         return name.toString();
      }

      public String formatCall(String args)
      {
         if (args != null && args.length() > 0) { args = ": " + args; }
         StringBuffer trace = new StringBuffer("[new " + name.getName() + args + "]");

         return trace.toString();
      }

      protected Scalar execute()
      {
         Object[] parameters = ObjectUtilities.buildArgumentArray(theConstructor.getParameterTypes(), getScriptEnvironment().getCurrentFrame(), getScriptEnvironment().getScriptInstance());

         try
         {
            return ObjectUtilities.BuildScalar(false, theConstructor.newInstance(parameters));
         }
         catch (InvocationTargetException ite)
         {
            if (ite.getCause() != null)
               getScriptEnvironment().flagError(ite.getCause());

            throw new RuntimeException(ite);
         }
         catch (IllegalArgumentException aex)
         {
            aex.printStackTrace();
            getScriptEnvironment().getScriptInstance().fireWarning(ObjectUtilities.buildArgumentErrorMessage(name, name.getName(), theConstructor.getParameterTypes(), parameters), getLineNumber());
         }
         catch (InstantiationException iex)
         {
            getScriptEnvironment().getScriptInstance().fireWarning("unable to instantiate abstract class " + name.getName(), getLineNumber());
         }
         catch (IllegalAccessException iax)
         {
            getScriptEnvironment().getScriptInstance().fireWarning("cannot access constructor in " + name.getName() + ": " + iax.getMessage(), getLineNumber());
         }

         return SleepUtils.getEmptyScalar();
      }
   }   

   //
   // Pre Condition:
   //   arguments are on the current frame
   //
   // Post Condition:
   //   current frame dissolved
   //   new object is placed on parent frame

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar      result;
      Constructor theConstructor  = ObjectUtilities.findConstructor(name, e.getCurrentFrame());

      if (theConstructor != null)
      {  
         try
         {
            theConstructor.setAccessible(true);
         }
         catch (Exception ex) { }
         ConstructorCallRequest request = new ConstructorCallRequest(e, getLineNumber(), theConstructor, name); 
         request.CallFunction();
         return null;
      }
      else
      {
         e.getScriptInstance().fireWarning("no constructor matching " + name.getName() + "("+SleepUtils.describe(e.getCurrentFrame()) + ")", getLineNumber());
         result = SleepUtils.getEmptyScalar();
         e.FrameResult(result);
      }

      return null;
   }
}
}