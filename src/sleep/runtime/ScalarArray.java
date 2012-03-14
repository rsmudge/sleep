/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
