/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------.
 | sleep.engine.atoms.Assign |__________________________________________________
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

public class Assign extends Step
{
   Block   variable = null;
   Step    operator = null;
    
   public Assign(Block var, Step op)
   {
      operator = op;
      variable = var;
   }

   public Assign(Block var)
   {
      this(var, null);
   }

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[Assign]:\n");
     
      temp.append(variable.toString(prefix + "   "));

      return temp.toString();
   }

   // Pre condition:
   //   actual right hand side value is on "current stack"
   //
   // Post condition:
   //   "current stack" is killed.
   
   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar putv, value;

      if (e.getCurrentFrame().size() > 1)
      {
         throw new RuntimeException("assignment is corrupted, did you forget a semicolon?");
      }

      // evaluate our left hand side (assign to) value

      e.CreateFrame();
         variable.evaluate(e);
         putv  = (Scalar)(e.getCurrentFrame().pop());
      e.KillFrame();

      value = (Scalar)(e.getCurrentFrame().pop());

      if (operator != null)
      {
         e.CreateFrame();
         e.getCurrentFrame().push(value); // rhs
         e.getCurrentFrame().push(putv);  // lhs - operate expects vars in a weird order.
         operator.evaluate(e);
         value = (Scalar)e.getCurrentFrame().pop();
      }

      putv.setValue(value);    
      e.FrameResult(value);
      return null;
   }
}



