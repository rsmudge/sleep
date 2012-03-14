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


