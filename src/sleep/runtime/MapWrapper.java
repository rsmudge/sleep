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

/** A class for creating accessing a Map data structure in your application in a ready only way.  It is assumed that your map 
data structure uses strings for keys.  Accessed values will be marshalled into Sleep scalars */
public class MapWrapper implements ScalarHash
{
   protected Map values;

   public MapWrapper(Map _values)
   {
      values = _values;
   }

   public Scalar getAt(Scalar key)
   {
      Object o = values.get(key.getValue().toString());
      return ObjectUtilities.BuildScalar(true, o);
   }

   /** this operation is kind of expensive... should be fixed up to take care of that */
   public ScalarArray keys()
   {
      return new CollectionWrapper(values.keySet());
   }

   public void remove(Scalar key)
   {
      throw new RuntimeException("hash is read-only");
   }

   public Map getData()
   {
      Map temp = new HashMap();
      Iterator i = values.entrySet().iterator();
      while (i.hasNext())
      {
         Map.Entry next = (Map.Entry)i.next();

         if (next.getValue() != null && next.getKey() != null)
         {
            temp.put(next.getKey().toString(), ObjectUtilities.BuildScalar(true, next.getValue()));
         }
      } 

      return temp;
   }

   public void rehash(int capacity, float load)
   {
      throw new RuntimeException("hash is read-only");
   }

   public String toString()
   {
      return values.toString();
   }
}
