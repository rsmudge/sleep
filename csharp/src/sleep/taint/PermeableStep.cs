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

using  sleep.engine;
using  sleep.interfaces;
using  sleep.runtime;


namespace sleep.taint{

public class PermeableStep : Step
{
   protected Step wrapped;

   public PermeableStep(Step step)
   {
      wrapped = step;
   }

   public void setInfo(int _line)
   {
      wrapped.setInfo(_line);
   }

   public int getLineNumber()
   {
      return wrapped.getLineNumber();
   }

   public override String toString(String prefix)
   {
      return prefix + "[Taint Wrap]\n" + wrapped.toString(prefix + "   ");
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      String desc = e.hasFrame() ? TaintUtils.checkArguments(e.getCurrentFrame()) : null;
      return callit(e, desc);
   }

   protected Scalar callit(ScriptEnvironment e, String desc)
   {
      wrapped.evaluate(e);

      if (desc != null && e.hasFrame() && !e.getCurrentFrame().isEmpty() && !SleepUtils.isEmptyScalar((Scalar)e.getCurrentFrame().peek()) && ((Scalar)e.getCurrentFrame().peek()).getActualValue() != null)
      {
         TaintUtils.taint((Scalar)e.getCurrentFrame().peek());

         if ((e.getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_TRACE_TAINT) == ScriptInstance.DEBUG_TRACE_TAINT)
         {
            e.getScriptInstance().fireWarning("tainted value: " + SleepUtils.describe((Scalar)e.getCurrentFrame().peek()) + " from: " + desc, getLineNumber());
         }

         return (Scalar)e.getCurrentFrame().peek();
      }

      return null;
   }
}
}