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


namespace sleep.runtime{

/** A read only scalar array for wrapping data structures that implement the java.util.Collection interface. 
Values will be marshalled into Sleep scalars when accessed. */
public class CollectionWrapper : ScalarArray
{
   protected java.util.Collection<Object> values;
   protected Object[]   array  = null;

   public ScalarArray sublist(int begin, int end)
   {
      java.util.List<Object> temp = new java.util.LinkedList<Object>();
      java.util.Iterator<Object> i = values.iterator();

      int count = 0;
      while (i.hasNext() && count < end)
      {
         Object tempo = i.next();

         if (count >= begin)
         {
            temp.add(tempo);
         }
         count++;
      }

      return new CollectionWrapper(temp);
   }  
 
   public CollectionWrapper(java.util.Collection<Object> _values)
   {
      values = _values;
   }

   public String toString()
   {
      return "(read-only array: " + values.toString() + ")";
   }

   public Scalar pop()
   {
      throw new java.lang.RuntimeException("array is read-only");
   }

   public void sort(java.util.Comparator<Object> compare)
   {
      throw new java.lang.RuntimeException("array is read-only");
   }

   public Scalar push(Scalar value)
   {
      throw new java.lang.RuntimeException("array is read-only");
   }

   public int size()
   {
      return values.size();
   }

   public Scalar remove(int index)
   {
      throw new java.lang.RuntimeException("array is read-only");
   }

   public Scalar getAt(int index)
   {
      if (array == null)
      {
         array = values.toArray();
      }

      return ObjectUtilities.BuildScalar(true, array[index]);
   }

   public java.util.Iterator<Object> scalarIterator()
   {
      return new ProxyIterator(values.iterator(), false);
   }

   public Scalar add(Scalar value, int index)
   {
      throw new java.lang.RuntimeException("array is read-only");
   }

   public void remove(Scalar value)
   {
      throw new java.lang.RuntimeException("array is read-only");
      // do nothing
   }

   public Type getType() {
      return GetType();
   }
}
}