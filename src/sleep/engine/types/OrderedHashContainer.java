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
package sleep.engine.types;

import sleep.runtime.*;

import java.util.*;

import sleep.bridges.*;

/* Container for Sleep hashes with associated policies.  This container allows construction of an ordered hash that orders elements based on
   insertion (or optionally access order).  A miss policy function determines what value to use when a non-existent key is requested.  The removal policy
   is called to determine if the last entry should be removed.  This is a powerful container for constructing caches out of the Sleep hash data structure. */  
public class OrderedHashContainer extends HashContainer 
{
   protected boolean shouldClean = false;

   private class OrderedHash extends LinkedHashMap
   {
      public OrderedHash(int c, float l, boolean b)
      {
         super(c, l, b);
      }

      protected boolean removeEldestEntry(Map.Entry eldest)
      {
         return removeEldestEntryCheck(eldest); 
      }
   }

   /** constructs an ordered hash container based on the specified items */
   public OrderedHashContainer(int capacity, float loadfactor, boolean type)
   {
       values = new OrderedHash(capacity, loadfactor, type);
   }

   /** policy function for what to do when a miss occurs */
   protected transient SleepClosure missPolicy;

   /** policy function for what to do when a hit occurs */
   protected transient SleepClosure removalPolicy;

   /** set the removal policy for this hash (decides if an entry should be removed or not */
   public void setRemovalPolicy(SleepClosure policy)
   {
      removalPolicy = policy;
   }

   /** set the miss policy for this hash (determines default value of missed value) */
   public void setMissPolicy(SleepClosure policy)
   {
      missPolicy = policy;
   }

   protected boolean removeEldestEntryCheck(Map.Entry eldest)
   {
      if (removalPolicy != null && eldest != null)
      {
         Stack locals = new Stack();
         locals.push(eldest.getValue());
         locals.push(SleepUtils.getScalar(eldest.getKey().toString()));
         locals.push(SleepUtils.getHashScalar(this));

         Scalar value = removalPolicy.callClosure("remove", null, locals);
         return SleepUtils.isTrueScalar(value);
      }

      return false;
   }

   public ScalarArray keys()
   {
      List keys = new LinkedList();
      Iterator i = values.entrySet().iterator();
      while (i.hasNext())
      {
         Map.Entry next = (Map.Entry)i.next();
         if (!SleepUtils.isEmptyScalar((Scalar)next.getValue()))
         {
            keys.add(next.getKey());
         }
      }      

      /* flag things for cleanup; the + 1 accounts for the possibility that we just
         accessed a new scalar that hasn't been assigned a value yet.  cleaning in this case would be pointless */
      shouldClean = values.size() > (keys.size() + 1);
      return new CollectionWrapper(keys);
   }

   /** removes all null values from this hash only when we get out of sync */
   private void cleanup()
   {
      if (shouldClean)
      {
         Iterator i = values.entrySet().iterator();
         while (i.hasNext())
         {
            Map.Entry next = (Map.Entry)i.next();
            if (SleepUtils.isEmptyScalar((Scalar)next.getValue()))
            {
               i.remove();
            }
         }      

         shouldClean = false;
      }
   }

   public Scalar getAt(Scalar key)
   {
      String temp = key.getValue().toString();
      Scalar value = (Scalar)values.get(temp);

      if (missPolicy != null && SleepUtils.isEmptyScalar(value))
      {
         cleanup();

         Stack locals = new Stack();
         locals.push(key);
         locals.push(SleepUtils.getHashScalar(this));

         value = SleepUtils.getScalar(missPolicy.callClosure("miss", null, locals));
         values.put(temp, value);
      }
      else if (value == null)
      {
         cleanup();

         value = SleepUtils.getEmptyScalar();
         values.put(temp, value);
      }

      return value;
   }
}
