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

public class Goto extends Step
{
   protected Block   iftrue;
   protected Check   start;
   protected Block   increment;

   public Goto (Check s)
   {
      start = s;
   }

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();
      temp.append(prefix);
      temp.append("[Goto]: \n");
      temp.append(prefix);
      temp.append("  [Condition]: \n");      
      temp.append(start.toString(prefix+"      "));
     
      if (iftrue != null)
      {
         temp.append(prefix); 
         temp.append("  [If true]:   \n");      
         temp.append(iftrue.toString(prefix+"      "));
      }

      if (increment != null)
      {
         temp.append(prefix); 
         temp.append("  [Increment]:   \n");      
         temp.append(increment.toString(prefix+"      "));
      }

      return temp.toString();
   }

   public void setIncrement(Block i)
   {
      increment = i;
   }

   public void setChoices(Block t)
   {
      iftrue = t;
   }

   public int getHighLineNumber()
   {
      return iftrue.getHighLineNumber();
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      while (!e.isReturn() && start.check(e))
      {
         iftrue.evaluate(e);

         if (e.getFlowControlRequest() == ScriptEnvironment.FLOW_CONTROL_CONTINUE)
         {
            e.clearReturn();

            if (increment != null)
            {
               increment.evaluate(e); /* normally this portion exists within iftrue but in the case of a continue
                                      the increment has to be executed separately so it is included */
            }
         }

         if (e.markFrame() >= 0)
         {
            e.getCurrentFrame().clear(); /* prevent some memory leakage action */
         }
      }

      if (e.getFlowControlRequest() == ScriptEnvironment.FLOW_CONTROL_BREAK)
      {
         e.clearReturn();
      }

      return null;
   }
}



