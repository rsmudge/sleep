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

using  sleep.bridges.SleepClosure;

using  java.lang.reflect;

namespace sleep.engine.atoms{

public class ObjectAccess : Step
{
   protected String name;
   protected Class  classRef;

   public ObjectAccess(String _name, Class _classRef)
   {
      name     = _name;
      classRef = _classRef;
   }

   public String toString()
   {
      return "[Object Access]: "+classRef+"#"+name+"\n";
   }

   private static class MethodCallRequest : CallRequest
   {
      protected Method theMethod;
      protected Scalar scalar;
      protected String name;
      protected Class  theClass;

      public MethodCallRequest(ScriptEnvironment e, int lineNo, Method method, Scalar _scalar, String _name, Class _class)
      {
         super(e, lineNo);
         theMethod = method;
         scalar    = _scalar;
         name      = _name;
         theClass  = _class;
      }

      public String getFunctionName()
      {
         return theMethod.toString();
      }

      public String getFrameDescription()
      {
         return theMethod.toString();   
      }

      public String formatCall(String args)
      {
         StringBuffer trace = new StringBuffer("[");

         if (args != null && args.length() > 0) { args = ": " + args; }

         if (scalar == null)
         {
            trace.append(theClass.getName() + " " + name + args + "]");
         }
         else
         {
            trace.append(SleepUtils.describe(scalar) + " " + name + args + "]");
         }

         return trace.toString();
      }

      protected Scalar execute()
      {
         Object[] parameters = ObjectUtilities.buildArgumentArray(theMethod.getParameterTypes(), getScriptEnvironment().getCurrentFrame(), getScriptEnvironment().getScriptInstance());

         try
         {
            return ObjectUtilities.BuildScalar(true, theMethod.invoke(scalar != null ? scalar.objectValue() : null, parameters));
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
            getScriptEnvironment().getScriptInstance().fireWarning(ObjectUtilities.buildArgumentErrorMessage(theClass, name, theMethod.getParameterTypes(), parameters), getLineNumber());
         }
         catch (IllegalAccessException iax)
         {
            getScriptEnvironment().getScriptInstance().fireWarning("cannot access " + name + " in " + theClass + ": " + iax.getMessage(), getLineNumber());
         }

         return SleepUtils.getEmptyScalar();
      }
   }
 
   //
   // Pre Condition:
   //   object we're accessing is top item on current frame
   //   arguments consist of the rest of the current frame...
   //
   // Post Condition:
   //   current frame is dissolved
   //   result is top item on parent frame

   public Scalar evaluate(ScriptEnvironment e)
   {
      Object accessMe = null;
      Class  theClass = null;
      Scalar scalar   = null;

      if (classRef == null)
      {
         scalar    = (Scalar)e.getCurrentFrame().pop();
         accessMe  = scalar.objectValue();

         if (accessMe == null)
         {
            e.getScriptInstance().fireWarning("Attempted to call a non-static method on a null reference", getLineNumber());
            e.KillFrame();
            e.getCurrentFrame().push(SleepUtils.getEmptyScalar());

            return null;
         }

         theClass  = accessMe.getClass();
      }
      else
      {
         theClass   = classRef;
      }
      
      //
      // check if this is a closure, if it is, try to invoke stuff on it instead
      //

      if (scalar != null && SleepUtils.isFunctionScalar(scalar))
      {
         CallRequest.ClosureCallRequest request = new CallRequest.ClosureCallRequest(e, getLineNumber(), scalar, name);
         request.CallFunction();
         return null;
      }

      //
      // now we know we're not dealing with a closure; so before we go on the name field has to be non-null.
      //

      if (name == null)
      {
         e.getScriptInstance().fireWarning("Attempted to query an object with no method/field", getLineNumber());
         e.KillFrame();
         e.getCurrentFrame().push(SleepUtils.getEmptyScalar());

         return null;
      }

      Scalar result = SleepUtils.getEmptyScalar();

      //
      // try to invoke stuff on the object...
      //

      Method theMethod = ObjectUtilities.findMethod(theClass, name, e.getCurrentFrame());

      if (theMethod != null && (classRef == null || (theMethod.getModifiers() & Modifier.STATIC) == Modifier.STATIC))
      {  
         try
         {
            theMethod.setAccessible(true);
         }
         catch (Exception ex) { }

         MethodCallRequest request = new MethodCallRequest(e, getLineNumber(), theMethod, scalar, name, theClass);
         request.CallFunction();
         return null;
      }
      else if (theMethod == null && !e.getCurrentFrame().isEmpty())
      {
         e.getScriptInstance().fireWarning("there is no method that matches " + name + "("+SleepUtils.describe(e.getCurrentFrame()) + ") in " + theClass.getName(), getLineNumber());
      }
      else
      {
         try
         {
            Field aField;

            try
            {
               aField = theClass.getDeclaredField(name);
            }
            catch (NoSuchFieldException nsfe)
            {
               aField = theClass.getField(name);
            }

            if (aField != null)
            {
               try
               {
                  aField.setAccessible(true);
               }
               catch (Exception ex) { }

               result = ObjectUtilities.BuildScalar(true, aField.get(accessMe));
            }
            else
            {
               result = SleepUtils.getEmptyScalar();
            }
         }
         catch (NoSuchFieldException fex)
         {
            e.getScriptInstance().fireWarning("no field/method named " + name + " in " + theClass, getLineNumber());
         }
         catch (IllegalAccessException iax)
         {
            e.getScriptInstance().fireWarning("cannot access " + name + " in " + theClass + ": " + iax.getMessage(), getLineNumber());
         }
      }

      e.FrameResult(result);
      return null;
   }
}
}