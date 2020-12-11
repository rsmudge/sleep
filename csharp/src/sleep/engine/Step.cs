/*
 * Copyright 2002-2020 Raphael Mudge
 * Coypright 2020 Sebastian Ritter
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
using  sleep.runtime;

namespace sleep.engine{
[Serializable]
public class Step : java.io.Serializable
{
   /** the script line number that this step was generated from */
   protected int  line;

   /** Steps act as a simple self contained linked list */
   public    Step next; 

   /** returns a string representation of this atomic step */
   public virtual String toString(String prefix)
   {
      return prefix+"[NOP]\n";
   }
 
   /** convience method for the code generator to set the line number. */
   public virtual void setInfo(int _line)
   {
      line = _line;
   }

   /** returns the last line number that this step is associated with (assuming it is
       associated with multiple lines */
   public virtual int getHighLineNumber()
   {
      return getLineNumber();
   }

   /** returns the first line number that this step is associated with (assuming it is
       associated with multiple lines */
   public virtual int getLowLineNumber()
   {
      return getLineNumber();
   }

   /** returns the line number this step is associated with */
   public virtual int getLineNumber()
   {
      return line;
   }

   /** evaluate this atomic step. */
   public virtual Scalar evaluate(ScriptEnvironment e) 
   {
      return SleepUtils.getEmptyScalar();
   }

   public virtual String toString()
   {
      return toString("");
   }
}

}