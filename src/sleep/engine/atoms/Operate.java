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

public class Operate extends Step
{
   String oper;

   public Operate(String o)
   {
       oper = o;
   }

   public String toString(String prefix)
   {
       return prefix + "[Operator]: "+oper+"\n";
   }

   //
   // Pre Condition:
   //   lhs, rhs are both on current frame
   //
   // Post Condition:
   //   current frame is dissolved
   //   return value of operation placed on parent frame
   //

   public Scalar evaluate(ScriptEnvironment e)
   {
      Operator callme = e.getOperator(oper);

      if (callme != null)
      {
         Scalar temp = callme.operate(oper, e.getScriptInstance(), e.getCurrentFrame());
         e.KillFrame();
         e.getCurrentFrame().push(temp);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to use non-existent operator: '" + oper + "'", getLineNumber());
         e.KillFrame();
         e.getCurrentFrame().push(SleepUtils.getEmptyScalar());
      }

      return null;
   }
}



