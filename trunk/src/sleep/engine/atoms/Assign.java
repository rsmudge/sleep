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



