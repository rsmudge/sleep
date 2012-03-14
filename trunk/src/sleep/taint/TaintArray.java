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
package sleep.taint;

import sleep.runtime.*;
import java.util.*;
import sleep.engine.ObjectUtilities;

/** Used to wrap read-only arrays so values are only converted on an as-needed basis */
public class TaintArray implements ScalarArray
{
   protected ScalarArray source;

   public ScalarArray sublist(int begin, int end)
   {
      return new TaintArray(source.sublist(begin, end));
   }

   public TaintArray(ScalarArray src)
   {
      source = src;
   }

   public String toString()
   {
      return source.toString();
   }

   public Scalar pop()
   {
      return TaintUtils.taintAll(source.pop());
   }

   public void sort(Comparator compare)
   {
      source.sort(compare);
   }

   public Scalar push(Scalar value)
   {
      return TaintUtils.taintAll(source.push(value));
   }

   public int size()
   {
      return source.size();
   }

   public Scalar remove(int index)
   {
      return TaintUtils.taintAll(source.remove(index));
   }

   public Scalar getAt(int index)
   {
      return TaintUtils.taintAll(source.getAt(index));
   }

   public Iterator scalarIterator()
   {
      return new TaintIterator(source.scalarIterator());
   }

   public Scalar add(Scalar value, int index)
   {
      return TaintUtils.taintAll(source.add(value, index));
   }

   public void remove(Scalar value)
   {
      source.remove(value);
   }

   protected class TaintIterator implements Iterator
   {
      protected Iterator realIterator;

      public TaintIterator(Iterator iter)
      {
         realIterator = iter;
      }

      public boolean hasNext()
      {
         return realIterator.hasNext(); 
      }

      public Object next()
      {
         return TaintUtils.taintAll((Scalar)realIterator.next());
      }

      public void remove()
      {
         realIterator.remove();
      }
   }
}
