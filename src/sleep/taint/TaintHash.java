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
package sleep.taint;

import sleep.runtime.*;
import java.util.*;
import sleep.engine.ObjectUtilities;

/** This class is used to wrap read-only hashes */
public class TaintHash implements ScalarHash
{
   protected ScalarHash source;

   public TaintHash(ScalarHash src)
   {
      source = src;
   }

   public Scalar getAt(Scalar key)
   {
      return TaintUtils.taintAll(source.getAt(key));
   }

   /** this operation is kind of expensive... should be fixed up to take care of that */
   public ScalarArray keys()
   {
      return source.keys();
   }

   public void remove(Scalar key)
   {
      source.remove(key);
   }

   public Map getData()
   {
      Map temp = source.getData();

      Iterator i = temp.entrySet().iterator();
      while (i.hasNext())
      {
         Map.Entry next = (Map.Entry)i.next();

         if (next.getValue() != null && next.getKey() != null)
         {
            next.setValue(TaintUtils.taintAll((Scalar)next.getValue())); 
         }
      } 

      return temp;
   }

   public String toString()
   {
      return source.toString();
   }
}
