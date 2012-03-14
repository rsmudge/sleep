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
package sleep.engine.atoms;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

import java.lang.reflect.*;

public class ObjectNew extends Step
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

   private static class ConstructorCallRequest extends CallRequest
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
