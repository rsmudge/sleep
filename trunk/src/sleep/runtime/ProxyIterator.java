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
