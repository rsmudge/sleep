/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------.
 | sleep.engine.atoms.Return |__________________________________________________
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

public class Return extends Step
{
   protected int return_type;

   /** See ScriptEnvironment.FLOW_CONTROL_* for the type constants */
   public Return(int type)
   {
      return_type = type;
   }

   public String toString(String prefix)
   {
      return prefix + "[Return]: " + return_type + " \n";
   }
  
   public Scalar evaluate(ScriptEnvironment e)
   {
      if (return_type == ScriptEnvironment.FLOW_CONTROL_THROW)
      {
         Scalar temp = (Scalar)e.getCurrentFrame().pop();
         if (!SleepUtils.isEmptyScalar(temp))
         {
            e.getScriptInstance().clearStackTrace();
            e.getScriptInstance().recordStackFrame("<origin of exception>", getLineNumber());
            e.flagReturn(temp, ScriptEnvironment.FLOW_CONTROL_THROW);
         }
      }
      else if (return_type == ScriptEnvironment.FLOW_CONTROL_BREAK || return_type == ScriptEnvironment.FLOW_CONTROL_CONTINUE)
      {
         e.flagReturn(null, return_type);
      }
      else if (return_type == ScriptEnvironment.FLOW_CONTROL_CALLCC)
      {
         Scalar temp = e.getCurrentFrame().isEmpty() ? SleepUtils.getEmptyScalar() : (Scalar)e.getCurrentFrame().pop();

         if (!SleepUtils.isFunctionScalar(temp))
         {
            e.getScriptInstance().fireWarning("callcc requires a function: " + SleepUtils.describe(temp), getLineNumber());
            e.flagReturn(temp, ScriptEnvironment.FLOW_CONTROL_YIELD);
         }
         else
         {
            e.flagReturn(temp, return_type);
         }
      }
      else if (e.getCurrentFrame().isEmpty())
      {
         e.flagReturn(SleepUtils.getEmptyScalar(), return_type);
      }
      else
      {
         e.flagReturn((Scalar)e.getCurrentFrame().pop(), return_type);
      }

      e.KillFrame();
      return null;
   }
}


