/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyrigth 2020 Sebastian Ritter
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
using  sleep.engine.types;
using  sleep.engine.atoms;
using  sleep.runtime;
using  sleep.interfaces;

namespace sleep.taint{


/** A replacement factory that generates Sleep interpreter instructions that honor and spread the taint mode. */
public class TaintModeGeneratedSteps : GeneratedSteps
{
   public Step Call(String function)
   {
      return new TaintCall(function, base.Call(function));
   }

   public Step PLiteral(java.util.List<Object> doit)
   {
      return new PermeableStep(base.PLiteral(doit));
   }

   public Step Operate(String oper)
   {
      return new TaintOperate(oper, base.Operate(oper));
   }

   public Step ObjectNew(Type name)
   {
      return new PermeableStep(base.ObjectNew(name));
   }

   public Step ObjectAccess(String name)     
   {
      return new TaintObjectAccess(base.ObjectAccess(name), name, null);
   }

   public override Step ObjectAccessStatic(Type aClass, String name)
   {
      return new TaintObjectAccess(base.ObjectAccessStatic(aClass, name), name, aClass);
   }
}
}