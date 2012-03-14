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

public class AssignT extends Step
{
   protected Step operator;

   public AssignT(Step op)
   {
      operator = op;
   }

   public AssignT()
   {
      this(null);
   }

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[AssignT]:\n");
     
      return temp.toString();
   }

   // Pre Condition:
   //   top value of "current frame" is the array value
   //   n values on "current frame" represent our assign to scalars
   //
   // Post Condition:
   //   "current frame" is dissolved.
   // 

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar   putv;
      Scalar   value;
      Iterator variter = null;

      Scalar scalar    = (Scalar)e.getCurrentFrame().pop(); /* source of our values */
      Scalar check     = (Scalar)e.getCurrentFrame().peek();

      if (e.getCurrentFrame().size() == 1 && check.getArray() != null && operator != null)
      {
         variter = check.getArray().scalarIterator();
      }
      else
      {
         variter = e.getCurrentFrame().iterator();
      }

      if (scalar.getArray() == null)
      {
         Iterator i = variter;
         while (i.hasNext())
         {
            putv = (Scalar)i.next();

            if (operator != null)
            {
               e.CreateFrame();
               e.CreateFrame();
               e.getCurrentFrame().push(scalar); // rhs
               e.getCurrentFrame().push(putv);  // lhs - operate expects vars in a weird order.
               operator.evaluate(e);
               putv.setValue((Scalar)e.getCurrentFrame().pop());
               e.KillFrame(); // need two frames, one for the operator atomic step and another
                              // to avoid a concurrent modification exception.
            }
            else
            {
               putv.setValue(scalar); // copying of value or ref handled by Scalar class
            }
         }          
         e.KillFrame();
         return null;
      }

      try {
      Iterator values = scalar.getArray().scalarIterator();
      Iterator putvs  = variter;

      while (putvs.hasNext())
      {
         putv = (Scalar)putvs.next();

         if (values.hasNext())
         {
            value = (Scalar)values.next();
         }
         else
         {
            value = SleepUtils.getEmptyScalar();
         }

         if (operator != null)
         {
            e.CreateFrame();
            e.CreateFrame();
            e.getCurrentFrame().push(value); // rhs
            e.getCurrentFrame().push(putv);  // lhs - operate expects vars in a weird order.
            operator.evaluate(e);
            value = (Scalar)e.getCurrentFrame().pop();
            e.KillFrame(); // see explanation above...
         }
 
         putv.setValue(value);
      }

      e.FrameResult(scalar);
      } catch (Exception ex) { ex.printStackTrace(); }
      return null;
   }
}



