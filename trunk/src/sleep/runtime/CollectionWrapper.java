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
import sleep.engine.ObjectUtilities;

/** A read only scalar array for wrapping data structures that implement the java.util.Collection interface. 
Values will be marshalled into Sleep scalars when accessed. */
public class CollectionWrapper implements ScalarArray
{
   protected Collection values;
   protected Object[]   array  = null;

   public ScalarArray sublist(int begin, int end)
   {
      List temp = new LinkedList();
      Iterator i = values.iterator();

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
 
   public CollectionWrapper(Collection _values)
   {
      values = _values;
   }

   public String toString()
   {
      return "(read-only array: " + values.toString() + ")";
   }

   public Scalar pop()
   {
      throw new RuntimeException("array is read-only");
   }

   public void sort(Comparator compare)
   {
      throw new RuntimeException("array is read-only");
   }

   public Scalar push(Scalar value)
   {
      throw new RuntimeException("array is read-only");
   }

   public int size()
   {
      return values.size();
   }

   public Scalar remove(int index)
   {
      throw new RuntimeException("array is read-only");
   }

   public Scalar getAt(int index)
   {
      if (array == null)
      {
         array = values.toArray();
      }

      return ObjectUtilities.BuildScalar(true, array[index]);
   }

   public Iterator scalarIterator()
   {
      return new ProxyIterator(values.iterator(), false);
   }

   public Scalar add(Scalar value, int index)
   {
      throw new RuntimeException("array is read-only");
   }

   public void remove(Scalar value)
   {
      throw new RuntimeException("array is read-only");
      // do nothing
   }
}
