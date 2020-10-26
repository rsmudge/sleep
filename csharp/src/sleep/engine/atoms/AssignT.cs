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

using  java.util;
using  sleep.interfaces;
using  sleep.engine;
using  sleep.runtime;

namespace sleep.engine.atoms{

public class AssignT : Step
{
   protected Step operatorJ;

   public AssignT(Step op)
   {
      operatorJ = op;
   }

   public AssignT()
   {
      this(null);
   }

   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[AssignT]:\n");
     
      return temp.toString();
   }

   // Pre Condition:
   //   top value of "current frame" is the array value
   //   n values on "current frame" represent our assign to scalars
   //
   // Post Condition:
   //   "current frame" is dissolved.
   // 

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar   putv;
      Scalar   value;
      Iterator variter = null;

      Scalar scalar    = (Scalar)e.getCurrentFrame().pop(); /* source of our values */
      Scalar check     = (Scalar)e.getCurrentFrame().peek();

      if (e.getCurrentFrame().size() == 1 && check.getArray() != null && operatorJ != null)
      {
         variter = check.getArray().scalarIterator();
      }
      else
      {
         variter = e.getCurrentFrame().iterator();
      }

      if (scalar.getArray() == null)
      {
         Iterator i = variter;
         while (i.hasNext())
         {
            putv = (Scalar)i.next();

            if (operatorJ != null)
            {
               e.CreateFrame();
               e.CreateFrame();
               e.getCurrentFrame().push(scalar); // rhs
               e.getCurrentFrame().push(putv);  // lhs - operate expects vars in a weird order.
               operatorJ.evaluate(e);
               putv.setValue((Scalar)e.getCurrentFrame().pop());
               e.KillFrame(); // need two frames, one for the operator atomic step and another
                              // to avoid a concurrent modification exception.
            }
            else
            {
               putv.setValue(scalar); // copying of value or ref handled by Scalar class
            }
         }          
         e.KillFrame();
         return null;
      }

      try {
      Iterator values = scalar.getArray().scalarIterator();
      Iterator putvs  = variter;

      while (putvs.hasNext())
      {
         putv = (Scalar)putvs.next();

         if (values.hasNext())
         {
            value = (Scalar)values.next();
         }
         else
         {
            value = SleepUtils.getEmptyScalar();
         }

         if (operatorJ != null)
         {
            e.CreateFrame();
            e.CreateFrame();
            e.getCurrentFrame().push(value); // rhs
            e.getCurrentFrame().push(putv);  // lhs - operate expects vars in a weird order.
            operatorJ.evaluate(e);
            value = (Scalar)e.getCurrentFrame().pop();
            e.KillFrame(); // see explanation above...
         }
 
         putv.setValue(value);
      }

      e.FrameResult(scalar);
      } catch (Exception ex) { ex.printStackTrace(); }
      return null;
   }
}



}