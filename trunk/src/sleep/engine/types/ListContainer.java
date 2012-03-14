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
package sleep.engine.types;

import sleep.runtime.*;
import java.util.*;

/** A linked list backing for Sleep Arrays. Most array ops are better off with this type of backing */
public class ListContainer implements ScalarArray
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
