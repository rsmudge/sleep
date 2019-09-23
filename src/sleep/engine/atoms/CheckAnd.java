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
import java.io.Serializable;
import sleep.runtime.*;

public class CheckAnd implements Check, Serializable
{
   private Check   left;
   private Check   right;

   /** Converts this object to a string, used by the sleep engine for constructing an AST like thing */
   public String toString(String prefix)
   {
       StringBuffer temp = new StringBuffer();
       temp.append(prefix);
       temp.append("[AND]:\n");
       temp.append(left.toString(prefix+"      "));
       temp.append(right.toString(prefix+"      "));
       return temp.toString();
   }

   /** Returns a string representation of this object */
   public String toString()
   {
       return toString("");
   }

   /** Constructs a check object, call by the sleep engine. */
   public CheckAnd(Check left, Check right) {
      this.left = left;
      this.right = right;
   }

   /** Performs this "check".  Returns the value of the condition that is checked. */
   public boolean check(ScriptEnvironment env)
   {
      return left.check(env) && right.check(env);
   }

   public void setInfo(int _hint) {}
}



