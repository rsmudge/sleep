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

public class Try extends Step
{
   Block owner, handler;
   String var;

   public Try (Block _owner, Block _handler, String _var)
   {
      owner   = _owner;
      handler = _handler;
      var     = _var;
   }

   public String toString(String prefix)
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append(prefix);
      buffer.append("[Try]\n");
      buffer.append(owner.toString(prefix + "   "));
      buffer.append(prefix);
      buffer.append("[Catch]: " + var + "\n");
      buffer.append(handler.toString(prefix + "   "));
      return buffer.toString();
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      int mark = e.markFrame();
      e.installExceptionHandler(owner, handler, var);
      Scalar o = owner.evaluate(e);
      e.cleanFrame(mark);
      return o;
   }
}



