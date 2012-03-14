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
package sleep.bridges;

/** A sleep synchronization primitive.  I know Java 1.5.0 has this stuff but since Sleep targets 1.4.2
    I get to provide my own.  How exciting. */
public class Semaphore
{
   private long count;

   /** initializes this semaphore with the specified initial count */
   public Semaphore(long initialCount)
   {
      count = initialCount;
   }

   /** aquires this semaphore by attempting to decrement the count.  blocks if the count is not > 0 (prior to decrement).  */
   public void P()
   {
      synchronized (this)
      {
         try
         {
            while (count <= 0)
            {
               wait();
            }

            count--;
         }
         catch (InterruptedException ex)
         { 
            ex.printStackTrace();
            notifyAll();
         }
      }
   }

   /** returns the current count data associated with this semaphore.  note that this value is volatile */
   public long getCount()
   {
      return count;
   }

   /** increments this semaphore */
   public void V()
   {
      synchronized (this)
      {
         count++;
         notifyAll();
      }
   }

   /** returns a nice string representation of this semaphore */
   public String toString()
   {
      return "[Semaphore: " + count + "]";
   } 
}
