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

public class BindPredicate extends Step
{
   String funcenv;
   Check pred;
   Block code;
 
   public String toString()
   {
      StringBuffer temp = new StringBuffer();
      temp.append("[Bind Predicate]: \n");
      temp.append("   [Pred]:       \n");
      temp.append(pred.toString("      "));
      temp.append("   [Code]:       \n");
      temp.append(code.toString("      "));

      return temp.toString();
   }

   public BindPredicate(String e, Check p, Block c)
   {
      funcenv = e;
      pred = p;
      code = c;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      PredicateEnvironment temp = e.getPredicateEnvironment(funcenv);
      
      if (temp != null)
      {
         temp.bindPredicate(e.getScriptInstance(), funcenv, pred, code);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to bind code to non-existent predicate environment: " + funcenv, getLineNumber());
      }

      return null;
   }
}



