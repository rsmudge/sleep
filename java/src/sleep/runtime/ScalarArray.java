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

/**
 * <p>This interface lets you implement your own data structure behind a scalar
 * array.</p>
 * 
 * <p>To instantiate a custom scalar array:</p>
 * 
 * <code>Scalar temp = SleepUtils.getArrayScalar(new MyScalarArray());</code>
 * 
 * <p>When implementing the following interface, keep in mind you are implementing an
 * interface to an array data structure.</p>
 */
public interface ScalarArray extends java.io.Serializable
{
   /** remove the topmost element from the array */
   public Scalar   pop();

   /** add an element onto the end of the array */
   public Scalar   push(Scalar value);

   /** return the size of the array */
   public int      size();

   /** get an element at the specified index */
   public Scalar   getAt(int index);

   /** return an iterator */
   public Iterator scalarIterator();

   /** add an element to the array at the specified index */
   public Scalar   add(Scalar value, int index); 

   /** remove all elements with the same identity as the specified scalar */
   public void     remove(Scalar value);

   /** remove an element at the specified index */
   public Scalar   remove(int index);

   /** sort this array with the specified comparator */
   public void     sort(Comparator compare);

   /** return a view into the array, ideally one that uses the same backing store */
   public ScalarArray sublist(int start, int end);
}
