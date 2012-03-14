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

public class Get extends Step
{
   String value;
  
   public Get(String v)
   {
      value = v;
   }

   public String toString(String prefix)
   {
      return prefix + "[Get Item]: "+value+"\n";
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      if (value.charAt(0) == '&')
      {
         Function func = e.getFunction(value);

         Scalar blah = SleepUtils.getScalar(func); 
         e.getCurrentFrame().push(blah);
      }
      else
      {
         Scalar structure = e.getScalar(value);

         if (structure == null)
         {
            if (value.charAt(0) == '@')
               structure = SleepUtils.getArrayScalar();
            else if (value.charAt(0) == '%')
               structure = SleepUtils.getHashScalar();
            else
               structure = SleepUtils.getEmptyScalar();

            e.putScalar(value, structure);

            if ((e.getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_REQUIRE_STRICT) == ScriptInstance.DEBUG_REQUIRE_STRICT)
            {
               e.showDebugMessage("variable '" + value + "' not declared");
            }
         }

         e.getCurrentFrame().push(structure);
      }

      return null;
   }
}



