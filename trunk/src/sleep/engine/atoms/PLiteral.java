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

import java.io.Serializable;

public class PLiteral extends Step
{
   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();
      temp.append(prefix);
      temp.append("[Parsed Literal] ");

      Iterator i = fragments.iterator();

      while (i.hasNext())
      {
         Fragment f = (Fragment)i.next();

         switch (f.type)
         {
            case STRING_FRAGMENT:
              temp.append(f.element);
              break;
            case ALIGN_FRAGMENT:
              temp.append("[:align:]");
              break;
            case VAR_FRAGMENT:
              temp.append("[:var:]");
              break;
         }
      }

      temp.append("\n");

      return temp.toString();
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar value = SleepUtils.getScalar(buildString(e));
      e.getCurrentFrame().push(value);
      return value;
   }

   public static final int STRING_FRAGMENT = 1;
   public static final int ALIGN_FRAGMENT  = 2;
   public static final int VAR_FRAGMENT    = 3;
  
   private static final class Fragment implements Serializable
   {
      public Object element;
      public int    type;
   }

   private List fragments;

   /** requires a list of parsed literal fragments to use when constructing the final string at runtime */
   public PLiteral(List f)
   {
      fragments = f;
   }

   /** create a fragment for interpretation by this parsed literal step */
   public static Fragment fragment(int type, Object element)
   {
      Fragment f = new Fragment();
      f.element  = element;
      f.type     = type;

      return f;
   }

   private String buildString(ScriptEnvironment e)
   {
      StringBuffer result = new StringBuffer();
      int          align  = 0;

      String       temp;
      Iterator i = fragments.iterator();

      while (i.hasNext())
      {
         Fragment f = (Fragment)i.next();

         switch (f.type)
         {
            case STRING_FRAGMENT:
              result.append(f.element);
              break;
            case ALIGN_FRAGMENT:
              align = ((Scalar)e.getCurrentFrame().remove(0)).getValue().intValue();
              break;
            case VAR_FRAGMENT:
              temp  = ((Scalar)e.getCurrentFrame().remove(0)).getValue().toString();

              for (int z = 0 - temp.length(); z > align; z--)
              {
                 result.append(" ");
              }

              result.append(temp);

              for (int y = temp.length(); y < align; y++)
              {
                 result.append(" ");
              }

              align = 0;              
              break;
         }
      }

      e.KillFrame();
      return result.toString();
   }
}



