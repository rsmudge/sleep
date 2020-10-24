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
package sleep.runtime;

import java.util.*;
import sleep.engine.ObjectUtilities;

/* An iterator wrapper that constructs Sleep compatible Scalars for 
   each iterator. */
public class ProxyIterator implements Iterator
{
   protected Iterator realIterator;
   protected boolean  modifyAllow;

   public ProxyIterator(Iterator iter, boolean _modifyAllow)
   {
      realIterator = iter;
      modifyAllow  = _modifyAllow;
   }

   /** Check if the Scalar contains a Java iterator value */
   public static boolean isIterator(Scalar value)
   {
      return value.getActualValue() != null && value.objectValue() instanceof Iterator;
   }

   public boolean hasNext()
   {
      return realIterator.hasNext(); 
   }

   public Object next()
   {
      Object temp = realIterator.next();
      return ObjectUtilities.BuildScalar(true, temp);
   }

   public void remove()
   {
      if (modifyAllow)
      {
         realIterator.remove();
      }
      else
      {
         throw new RuntimeException("iterator is read-only");
      }
   }
}
