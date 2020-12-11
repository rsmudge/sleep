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
/** A sensitive function */
public class Sensitive : Function
{
   protected Object function;

   public Sensitive(Object f)
   {      
      function = f;
   }   

   public Scalar evaluate(String name, ScriptInstance script, java.util.Stack<Object> arguments)
   {
      java.util.Stack<Object> dangers = new java.util.Stack<Object>();
      java.util.Iterator<Object> i = arguments.iterator();
      while (i.hasNext())
      {
         Scalar next = (Scalar)i.next();

         if (TaintUtils.isTainted(next))
         {
            dangers.push(next);
         }
      }

      if (dangers.isEmpty())
      {
         return ((Function)function).evaluate(name, script, arguments);
      }
      else
      {
         throw new java.lang.RuntimeException("Insecure " + name + ": " + SleepUtils.describe(dangers) + " is tainted");
      }
   }
}
}