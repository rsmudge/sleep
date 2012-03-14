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

/* Container for Sleep hashes.  *phEAR* */
public class HashContainer implements ScalarHash
{
   protected Map values;

   /* constructs this hash container using the specified Map compatible data structure as the
      backing.  this data structure will hold sleep.runtime.Scalar objects and should by empty
      when passed to this constructor */
   public HashContainer(Map container)
   {
      values = container;
   }

   /* constructs this hash container backed by a HashMap data structure */
   public HashContainer()
   {
      this(new HashMap());
   }

   public Scalar getAt(Scalar key)
   {
      String temp = key.getValue().toString();
      Scalar value = (Scalar)values.get(temp);

      if (value == null)
      {
         value = SleepUtils.getEmptyScalar();
         values.put(temp, value);
      }

      return value;
   }

   public Map getData()
   {
      return values;
   }

   public ScalarArray keys()
   {
      ScalarType ntype = SleepUtils.getEmptyScalar().getValue();

      Iterator i = values.values().iterator();
      while (i.hasNext())
      {
         Scalar next = (Scalar)i.next();

         if (next.getArray() == null && next.getHash() == null && next.getActualValue() == ntype)
         {
            i.remove();
         }
      }

      return new CollectionWrapper(values.keySet());
   }

   public void remove(Scalar value)
   {
      SleepUtils.removeScalar(values.values().iterator(), value);
   }

   public String toString()
   {
      return values.toString();
   }
}
