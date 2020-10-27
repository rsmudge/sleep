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
using  sleep.bridges;

namespace sleep.engine.atoms{

public class Index : Step
{
   String value; /* the name of the original data structure we are accessing, important for creating a new ds if we have to */
   Block index;  

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[Scalar index]: "+value+"\n");

      if (index != null)
      {
         temp.append(index.toString(prefix + "   "));
      }

      return temp.toString();
   }

   public Index(String v, Block i)
   {
      value = v;
      index = i;
   }

   //
   // Pre Condition:
   //   previous data structure is top item on current frame
   //
   // Post Condition:
   //   current frame is dissolved
   //   current data data structure is top item on parent frame

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar pos, rv = null;

      Scalar structure = (Scalar)e.getCurrentFrame().pop();

      if (SleepUtils.isEmptyScalar(structure))
      {
          if (value.charAt(0) == '@')
          {
             structure.setValue(SleepUtils.getArrayScalar());
          }
          else if (value.charAt(0) == '%')
          {
             structure.setValue(SleepUtils.getHashScalar());
          }
      }

//      e.CreateFrame();
         index.evaluate(e);
         pos = (Scalar)(e.getCurrentFrame().pop());
//      e.KillFrame();

      if (structure.getArray() != null) 
      { 
          int posv = pos.getValue().intValue();

          if (posv < 0)
          {
             int size = structure.getArray().size();
             while (posv < 0)
             {
                posv += size;
             }
          }
          
          rv = structure.getArray().getAt(posv); 
      }
      else if (structure.getHash() != null) { rv = structure.getHash().getAt(pos); }
      else if (structure.objectValue() != null && structure.objectValue() is SleepClosure)
      {
         SleepClosure closure = (SleepClosure)structure.objectValue();

         if (!closure.getVariables().scalarExists(pos.toString()))
         {
            closure.getVariables().putScalar(pos.toString(), SleepUtils.getEmptyScalar());
         }
         rv = closure.getVariables().getScalar(pos.toString());
      }
      else 
      { 
         e.KillFrame();
         throw new IllegalArgumentException("invalid use of index operator: " + SleepUtils.describe(structure) + "[" + SleepUtils.describe(pos) + "]");
      } 

      e.FrameResult(rv);
      return null;
   }
}
}