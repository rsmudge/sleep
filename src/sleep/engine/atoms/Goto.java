/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------.
 | sleep.engine.atoms.Goto |__________________________________________________
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



