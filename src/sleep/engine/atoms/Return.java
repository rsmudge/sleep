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
import sleep.runtime.*;

public class Return extends Step
{
   protected int return_type;

   /** See ScriptEnvironment.FLOW_CONTROL_* for the type constants */
   public Return(int type)
   {
      return_type = type;
   }

   public String toString(String prefix)
   {
      return prefix + "[Return]: " + return_type + " \n";
   }
  
   public Scalar evaluate(ScriptEnvironment e)
   {
      if (return_type == ScriptEnvironment.FLOW_CONTROL_THROW)
      {
         Scalar temp = (Scalar)e.getCurrentFrame().pop();
         if (!SleepUtils.isEmptyScalar(temp))
         {
            e.getScriptInstance().clearStackTrace();
            e.getScriptInstance().recordStackFrame("<origin of exception>", getLineNumber());
            e.flagReturn(temp, ScriptEnvironment.FLOW_CONTROL_THROW);
         }
      }
      else if (return_type == ScriptEnvironment.FLOW_CONTROL_BREAK || return_type == ScriptEnvironment.FLOW_CONTROL_CONTINUE)
      {
         e.flagReturn(null, return_type);
      }
      else if (return_type == ScriptEnvironment.FLOW_CONTROL_CALLCC)
      {
         Scalar temp = e.getCurrentFrame().isEmpty() ? SleepUtils.getEmptyScalar() : (Scalar)e.getCurrentFrame().pop();

         if (!SleepUtils.isFunctionScalar(temp))
         {
            e.getScriptInstance().fireWarning("callcc requires a function: " + SleepUtils.describe(temp), getLineNumber());
            e.flagReturn(temp, ScriptEnvironment.FLOW_CONTROL_YIELD);
         }
         else
         {
            e.flagReturn(temp, return_type);
         }
      }
      else if (e.getCurrentFrame().isEmpty())
      {
         e.flagReturn(SleepUtils.getEmptyScalar(), return_type);
      }
      else
      {
         e.flagReturn((Scalar)e.getCurrentFrame().pop(), return_type);
      }

      e.KillFrame();
      return null;
   }
}


