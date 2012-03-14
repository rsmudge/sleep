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
import sleep.engine.types.*;
import sleep.runtime.*;

public class Call extends Step
{
   String function;
 
   public Call(String f)
   {
      function = f;
   }

   public String toString(String prefix)
   {
      return prefix + "[Function Call]: "+function+"\n";
   }

   // Pre Condition:
   //  arguments on the current stack (to allow stack to be passed0
   //
   // Post Condition:
   //  current frame will be dissolved and return value will be placed on parent frame

   public Scalar evaluate(ScriptEnvironment e)
   {
      Function callme = e.getFunction(function);
      Block    inline = null;

      if (callme != null)
      {
         CallRequest.FunctionCallRequest request = new CallRequest.FunctionCallRequest(e, getLineNumber(), function, callme);         
         request.CallFunction();
      }
      else if ((inline = e.getBlock(function)) != null)
      {
         CallRequest.InlineCallRequest request = new CallRequest.InlineCallRequest(e, getLineNumber(), function, inline);
         request.CallFunction();
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempted to call non-existent function " + function, getLineNumber());
         e.FrameResult(SleepUtils.getEmptyScalar());
      }

      return null;
   }
}
