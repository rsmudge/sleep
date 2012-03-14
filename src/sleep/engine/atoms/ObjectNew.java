/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------.
 | sleep.engine.atoms.Call |__________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/

   Description: This class contains an implementation of an atomic Step for
     the sleep scripting.  

   Documentation:

   Changelog:
   11/17/2002 - this class was refactored out of Step and put in its own file.

   * This software is distributed under the artistic license, see license.txt
     for more information. *

 |____________________________________________________________________________|
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
