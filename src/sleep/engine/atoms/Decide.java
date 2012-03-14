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

public class Decide extends Step
{
   public Block iftrue;
   public Block iffalse;
   public Check start;
 
   public Decide (Check s)
   {
      start = s;
   }

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();
      temp.append(prefix);
      temp.append("[Decide]:\n");
      temp.append(prefix);
      temp.append("  [Condition]: \n");      
      temp.append(start.toString(prefix+"      "));
     
      if (iftrue != null)
      {
         temp.append(prefix); 
         temp.append("  [If true]:   \n");      
         temp.append(iftrue.toString(prefix+"      "));
      }

      if (iffalse != null)
      {
         temp.append(prefix); 
         temp.append("  [If False]:   \n");      
         temp.append(iffalse.toString(prefix+"      "));
      }

      return temp.toString();
   }

   public int getHighLineNumber()
   {
      if (iftrue == null)
      {
         return iffalse.getHighLineNumber();
      }
      else if (iffalse == null)
      {
         return iftrue.getHighLineNumber();
      }
      int x = iftrue.getHighLineNumber(); 
      int y = iffalse.getHighLineNumber();
      return x > y ? x : y;
   }

   public void setChoices(Block t, Block f)
   {
      iftrue  = t;
      iffalse = f;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      if (start.check(e))
      {
          if (iftrue != null) { iftrue.evaluate(e); }
      }
      else if (iffalse != null)
      {
          iffalse.evaluate(e);
      }

      return null;
   }
}



