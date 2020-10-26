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

using  sleep.runtime;
using  java.util;

namespace sleep.engine.types{

/** A linked list backing for Sleep Arrays. Most array ops are better off with this type of backing */
public class ListContainer : ScalarArray
{
   protected List values;

   public ListContainer()
   {
      values = new MyLinkedList();
   }

   public ListContainer(List list)
   {
      values = list;
   }

   public ScalarArray sublist(int from, int to)
   {
      return new ListContainer((List)values.subList(from, to));
   }

   /** initial values must be a collection of Scalar's */
   public ListContainer(Collection initialValues)
   {
      this();
      values.addAll(initialValues);
   }

   public Scalar pop()
   {
      return (Scalar)values.remove(values.size() - 1);
   }

   public Scalar push(Scalar value)
   {
      values.add(value);
      return value;
   }

   public int size()
   {
      return values.size();
   }

   public void sort(Comparator compare)
   {
      Collections.sort(values, compare);
   }

   public Scalar getAt(int index)
   {
      if (index >= size())
      {
          Scalar temp = SleepUtils.getEmptyScalar();
          values.add(temp);
          return temp;   
      }

      return (Scalar)values.get(index);
   }

   public void remove(Scalar key)
   {
      SleepUtils.removeScalar(values.iterator(), key);
   }

   public Scalar remove(int index)
   {
      return (Scalar)values.remove(index);
   }

   public Iterator scalarIterator()
   {
      return values.iterator();
   }

   public Scalar add(Scalar value, int index)
   {
      values.add(index, value);
      return value;
   }

   public String toString()
   {
      return values.toString();
   }
}
}