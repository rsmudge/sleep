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

public class Decide : Step
{
   public Block iftrue;
   public Block iffalse;
   public Check start;
 
   public Decide (Check s)
   {
      start = s;
   }

   public String toString(String prefix)
   {
      java.lang.StringBuffer temp = new java.lang.StringBuffer();
      temp.append(prefix);
      temp.append("[Decide]:\n");
      temp.append(prefix);
      temp.append("  [Condition]: \n");      
      temp.append(start.toString(prefix+"      "));
     
      if (iftrue != null)
      {
         temp.append(prefix); 
         temp.append("  [If true]:   \n");      
         temp.append(iftrue.toString(prefix+"      "));
      }

      if (iffalse != null)
      {
         temp.append(prefix); 
         temp.append("  [If False]:   \n");      
         temp.append(iffalse.toString(prefix+"      "));
      }

      return temp.toString();
   }

   public int getHighLineNumber()
   {
      if (iftrue == null)
      {
         return iffalse.getHighLineNumber();
      }
      else if (iffalse == null)
      {
         return iftrue.getHighLineNumber();
      }
      int x = iftrue.getHighLineNumber(); 
      int y = iffalse.getHighLineNumber();
      return x > y ? x : y;
   }

   public void setChoices(Block t, Block f)
   {
      iftrue  = t;
      iffalse = f;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      if (start.check(e))
      {
          if (iftrue != null) { iftrue.evaluate(e); }
      }
      else if (iffalse != null)
      {
          iffalse.evaluate(e);
      }

      return null;
   }
}



}