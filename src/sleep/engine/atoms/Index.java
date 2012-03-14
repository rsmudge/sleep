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
import sleep.bridges.SleepClosure;

public class Index extends Step
{
   String value; /* the name of the original data structure we are accessing, important for creating a new ds if we have to */
   Block index;  

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[Scalar index]: "+value+"\n");

      if (index != null)
      {
         temp.append(index.toString(prefix + "   "));
      }

      return temp.toString();
   }

   public Index(String v, Block i)
   {
      value = v;
      index = i;
   }

   //
   // Pre Condition:
   //   previous data structure is top item on current frame
   //
   // Post Condition:
   //   current frame is dissolved
   //   current data data structure is top item on parent frame

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar pos, rv = null;

      Scalar structure = (Scalar)e.getCurrentFrame().pop();

      if (SleepUtils.isEmptyScalar(structure))
      {
          if (value.charAt(0) == '@')
          {
             structure.setValue(SleepUtils.getArrayScalar());
          }
          else if (value.charAt(0) == '%')
          {
             structure.setValue(SleepUtils.getHashScalar());
          }
      }

//      e.CreateFrame();
         index.evaluate(e);
         pos = (Scalar)(e.getCurrentFrame().pop());
//      e.KillFrame();

      if (structure.getArray() != null) 
      { 
          int posv = pos.getValue().intValue();

          if (posv < 0)
          {
             int size = structure.getArray().size();
             while (posv < 0)
             {
                posv += size;
             }
          }
          
          rv = structure.getArray().getAt(posv); 
      }
      else if (structure.getHash() != null) { rv = structure.getHash().getAt(pos); }
      else if (structure.objectValue() != null && structure.objectValue() instanceof SleepClosure)
      {
         SleepClosure closure = (SleepClosure)structure.objectValue();

         if (!closure.getVariables().scalarExists(pos.toString()))
         {
            closure.getVariables().putScalar(pos.toString(), SleepUtils.getEmptyScalar());
         }
         rv = closure.getVariables().getScalar(pos.toString());
      }
      else 
      { 
         e.KillFrame();
         throw new IllegalArgumentException("invalid use of index operator: " + SleepUtils.describe(structure) + "[" + SleepUtils.describe(pos) + "]");
      } 

      e.FrameResult(rv);
      return null;
   }
}
