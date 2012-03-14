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

public class BindFilter extends Step
{
   String funcenv;
   Block  code;
   String filter;
   String name;
 
   public String toString()
   {
      StringBuffer temp = new StringBuffer();
      temp.append("[Bind Filter]: "+name+"\n");
      temp.append("   [Filter]:       \n");
      temp.append("      " + filter.toString());
      temp.append("   [Code]:       \n");
      temp.append(code.toString("      "));

      return temp.toString();
   }

   public BindFilter(String e, String n, Block c, String f)
   {
      funcenv = e;
      code    = c;
      filter  = f;
      name    = n;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      FilterEnvironment temp = e.getFilterEnvironment(funcenv);
      
      if (temp != null)
      {
         temp.bindFilteredFunction(e.getScriptInstance(), funcenv, name, filter, code);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to bind code to non-existent predicate environment: " + funcenv, getLineNumber());
      }

      return null;
   }
}



