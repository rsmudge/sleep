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

public class Assign : Step
{
   Block   variable = null;
   Step    operatorJ = null;
    
   public Assign(Block var, Step op)
   {
      operatorJ = op;
      variable = var;
   }

   public Assign(Block var)
   {
      this(var, null);
   }

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[Assign]:\n");
     
      temp.append(variable.toString(prefix + "   "));

      return temp.toString();
   }

   // Pre condition:
   //   actual right hand side value is on "current stack"
   //
   // Post condition:
   //   "current stack" is killed.
   
   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar putv, value;

      if (e.getCurrentFrame().size() > 1)
      {
         throw new RuntimeException("assignment is corrupted, did you forget a semicolon?");
      }

      // evaluate our left hand side (assign to) value

      e.CreateFrame();
         variable.evaluate(e);
         putv  = (Scalar)(e.getCurrentFrame().pop());
      e.KillFrame();

      value = (Scalar)(e.getCurrentFrame().pop());

      if (operatorJ != null)
      {
         e.CreateFrame();
         e.getCurrentFrame().push(value); // rhs
         e.getCurrentFrame().push(putv);  // lhs - operate expects vars in a weird order.
         operatorJ.evaluate(e);
         value = (Scalar)e.getCurrentFrame().pop();
      }

      putv.setValue(value);    
      e.FrameResult(value);
      return null;
   }
}



}