/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
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
using System;
using java = biz.ritter.javapi;

using  sleep.interfaces;
using  sleep.engine;
using  sleep.runtime;

namespace sleep.engine.atoms{

public class Operate : Step
{
   String oper;

   public Operate(String o)
   {
       oper = o;
   }

   public String toString(String prefix)
   {
       return prefix + "[Operator]: "+oper+"\n";
   }

   //
   // Pre Condition:
   //   lhs, rhs are both on current frame
   //
   // Post Condition:
   //   current frame is dissolved
   //   return value of operation placed on parent frame
   //

   public Scalar evaluate(ScriptEnvironment e)
   {
      Operator callme = e.getOperator(oper);

      if (callme != null)
      {
         Scalar temp = callme.operate(oper, e.getScriptInstance(), e.getCurrentFrame());
         e.KillFrame();
         e.getCurrentFrame().push(temp);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to use non-existent operator: '" + oper + "'", getLineNumber());
         e.KillFrame();
         e.getCurrentFrame().push(SleepUtils.getEmptyScalar());
      }

      return null;
   }
}



}