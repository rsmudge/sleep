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

public class Bind extends Step
{
   String funcenv;
   Block code, name;
 
   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[Bind Function]: \n");

      temp.append(prefix);
      temp.append("   [Name]:       \n");

      temp.append(prefix);
      temp.append(name.toString(prefix + "      "));

      temp.append(prefix);
      temp.append("   [Code]:       \n");

      temp.append(prefix);
      temp.append(code.toString(prefix + "      "));

      return temp.toString();
   }

   public Bind(String e, Block n, Block c)
   {
      funcenv = e;
      name = n;
      code = c;
   }

   //
   // no stack pre/post conditions for this step
   //

   public Scalar evaluate(ScriptEnvironment e)
   {
      Environment temp = e.getFunctionEnvironment(funcenv);
      
      if (temp != null)
      { 
         e.CreateFrame();
            name.evaluate(e);
            Scalar funcname = (Scalar)e.getCurrentFrame().pop();
         e.KillFrame();

         temp.bindFunction(e.getScriptInstance(), funcenv, funcname.getValue().toString(), code);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to bind code to non-existent environment: " + funcenv, getLineNumber());
      }

      return null;
   }
}



