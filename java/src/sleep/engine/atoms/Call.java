/*
 * Copyright 2002-2020 Raphael Mudge
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
