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
