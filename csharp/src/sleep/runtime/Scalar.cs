/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
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
using System;
using java = biz.ritter.javapi;


namespace sleep.runtime{

/**
 * <p>A scalar is the universal data type for sleep variables.  Scalars can have numerical values of integer, double, or 
 * long.  Scalars can have a string value.  Scalars can also contain a reference to a scalar array, scalar hash, or a 
 * generic Java object.  
 * 
 * <p>Numerical and String values are stored as ScalarTypes.  Arrays and Hashes are stored in ScalarArray and 
 * ScalarHash containers respectively.</p>
 * 
 * <h3>Instantiating a Scalar</h3>
 * 
 * <p>Instantiating a Scalar is most easily done using the sleep.runtime.SleepUtils class.  The SleepUtils class contains 
 * several static methods for creating a Scalar object from data.</p>
 * 
 * <p>The general pattern for this is a {@link sleep.runtime.SleepUtils#getScalar(String) SleepUtils.getScalar(data)} methods.  There are 
 * static getScalar() methods that take a double, int, long, Object, or a String as a parameter.</p>
 * 
 * <p>There are even methods for wrapping java data structures into a scalar array or scalar hash.  Methods also exist to 
 * copy data from one scalar into another new scalar.</p>
 * 
 * <p>Examples:</b>
 * 
 * <pre>
 * Scalar anInt   = SleepUtils.getScalar(3); // create an int scalar
 * Scalar aDouble = SleepUtils.getScalar(4.5); // create a double scalar
 * Scalar aString = SleepUtils.getScalar("hello"); // string scalar
 * Scalar anArray = SleepUtils.getArrayWrapper(new LinkedList(); // array scalar
 * </pre>
 * 
 * <h3>Working with Scalar Arrays</h3>
 * 
 * <p>To add a value to a Scalar array:</p>
 * 
 * <pre>
 * Scalar arrayScalar = SleepUtils.getArray(); // empty array
 * arrayScalar.getArray().add(SleepUtils.getScalar("value"), 0);
 * </pre>
 * 
 * <p>To iterate through all of the values in a Scalar array:</p>
 * 
 * <pre>
 * Iterator i = arrayScalar.getArray().scalarIterator();
 * while (i.hasNext())
 * {
 *     Scalar temp = (Scalar)i.next();
 * }
 * </pre>
 * 
 * <h3>Working with Scalar Hashes</h3>
 * 
 * <p>To add a value to a Scalar hashtable:</p>
 * 
 * <pre>
 * Scalar hashScalar = SleepUtils.getHashScalar(); // blank hashtable
 * Scalar temp = hashScalar.getHash().getAt(SleepUtils.getScalar("key"));
 * temp.setValue(SleepUtils.getScalar("value"));
 * </pre>
 * 
 * <p>The second line obtains a Scalar for "key". The returned Scalar is just a container.   It is possible to set the 
 * value of the returned scalar using the setValue method.</p>
 * 
 * <p>Internally scalar values in sleep are passed by value.  Methods like setValue inside of the Scalar class take care of 
 * copying the value.  Externally though Scalar objects are passed by reference.  When you call getAt() in the ScalarHash 
 * you are obtaining a reference to a Scalar inside of the hashtable.  When you change the value of the Scalar you 
 * obtained, you change the value of the Scalar in the hashtable.</p>
 * 
 * @see sleep.runtime.SleepUtils
 * @see sleep.runtime.ScalarType
 * @see sleep.runtime.ScalarArray
 * @see sleep.runtime.ScalarHash
 */
 [Serializable]
public class Scalar : java.io.Serializable
{
   protected ScalarType  value = null;
   protected ScalarArray array = null;
   protected ScalarHash  hash  = null;

   /** Returns the actual non-array/non-hash value this scalar contains.  This is mainly for use by internal sleep
       classes that do not want to accidentally convert a hash/array to a string. */
   public ScalarType getActualValue()
   {
      return value;
   }

   /** Returns the container for the scalars value.  If this is an array or hash scalar then they will be converted to a string 
       scalar and returned.  If this scalar is completely null then null will be returned which will mess up the interpreter 
       somewhere */
   public ScalarType getValue()
   {
      if (value != null)
         return value;

      /* these are in case the scalar is being misused */

      if (array != null)
         return SleepUtils.getScalar(SleepUtils.describe(this)).getValue();

      if (hash != null)
         return SleepUtils.getScalar(SleepUtils.describe(this)).getValue();

      return null;
   }

   /** the string value of this scalar */
   public String stringValue()
   {
      return getValue().toString();
   }

   /** the int value of this scalar */
   public int intValue()
   {
      return getValue().intValue();
   }

   /** the double value of this scalar */
   public double doubleValue()
   {
      return getValue().doubleValue();
   }

   /** the long value of this scalar */
   public long longValue()
   {
      return getValue().longValue();
   }

   /** the object value of this scalar */
   public Object objectValue()
   {
      if (array != null)
         return array;

      if (hash  != null)
         return hash;

      return value.objectValue();
   }

   /** returns a scalar array referenced by this scalar iff this scalar contains an array reference */
   public ScalarArray getArray()
   {
      return array;
   }

   /** returns a scalar hash referenced by this scalar iff this scalar contains a hash reference */
   public ScalarHash getHash()
   {
      return hash;
   }

   /** set the value of this scalar container to a scalar value of some type */
   public void setValue(ScalarType _value)
   {
      value = _value.copyValue();
      array = null;
      hash  = null;
   }

   /** set the value of this scalar container to a scalar array */
   public void setValue(ScalarArray _array)
   {
      value = null;
      array = _array;
      hash  = null;
   }

   /** set the value of this scalar container to a scalar hash */
   public void setValue(ScalarHash _hash)
   {
      value = null;
      array = null;
      hash  = _hash;
   }

   /** returns an identity value for this scalar.  the identity value is used in set operations.  basically any scalar values
       that are handled by reference (object,s arrays, and hashes) use their reference as their identity.  other values used
       their string value as their identity (doubles that do not have a decimal point will be converted to longs). */
   public Object identity()
   {
      if (this.getArray() != null) { return array; }
      if (this.getHash() != null) { return hash; }
      if (value.getType() == typeof(sleep.engine.types.ObjectValue)) { return this.objectValue(); }
      return this.toString();
   }
 
   /** compares two scalars in terms of their identity.  scalars that hold references (array, object, and hash) are compared by
       reference where other values are compared by their string value.  doubles with a round value will be converted to a long */
   public bool sameAs(Scalar other)
   {
      if (this.getArray() != null && other.getArray() != null && this.getArray() == other.getArray())
      {
         return true;
      } 
      else if (this.getHash() != null && other.getHash() != null && this.getHash() == other.getHash())
      {
         return true;
      }
      else if (this.getActualValue() != null && other.getActualValue() != null)
      {
         if (this.getActualValue().getType() == typeof(sleep.engine.types.ObjectValue) || other.getActualValue().getType() == typeof(sleep.engine.types.ObjectValue))
         {
            return (this.objectValue() == other.objectValue());
         }
         else 
         { 
            return this.identity().equals(other.identity());
         }
      }
 
      return false;
   }

   public String toString()
   {
      return stringValue();
   }

   /** clones the value from the specified scalar and gives this scalar a copy of the value */
   public void setValue(Scalar newValue)
   {
      if (newValue == null) { return; }
      if (newValue.getArray() != null) { setValue(newValue.getArray()); return; }
      if (newValue.getHash()  != null) { setValue(newValue.getHash()); return; }
      if (newValue.getValue() != null) { setValue(newValue.getValue()); return; }
   }

   private void writeObject(java.io.ObjectOutputStream outJ) //throws IOException
   {
       if (SleepUtils.isEmptyScalar(this))
       {
          outJ.writeObject(null);
       }
       else
       {
          outJ.writeObject(value);
       }
       outJ.writeObject(array);       
       outJ.writeObject(hash);       
   }

   private void readObject(java.io.ObjectInputStream inJ) //throws IOException, ClassNotFoundException
   {
       value = (ScalarType)inJ.readObject();
       array = (ScalarArray)inJ.readObject();
       hash  = (ScalarHash)inJ.readObject();
   
       if (value == null && array == null && hash == null)
       {
          setValue(SleepUtils.getEmptyScalar());
       }
   }
}
}