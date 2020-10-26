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

using  java.util;


namespace sleep.runtime{

/**
 * <p>This interface lets you create your own scalar hash implementation.</p>
 * 
 * <p>To create a new type of scalar hash: create a class that implements the sleep.runtime.ScalarHash interface.  The 
 * scalar hash interface asks for methods that define all of the common operations on sleep hashes.</p>
 * 
 * <p>To instantiate a custom scalar hash:</p>
 * 
 * <code>Scalar temp = SleepUtils.getHashScalar(new MyHashScalar());</code>
 * 
 * <p>In the above example MyHashScalar is the class name of your new scalar hash implementation.</p>
 * 
 * <p>Keep in mind when implementing the interface below that you are defining the interface to a dictionary style
 * data structure.</p>
 */
 [Serializable]
public interface ScalarHash : java.io.Serializable
{
   /** Retrieves a scalar from the hashtable.  If a scalar key does not exist then the key should be created with a 
       value of $null.  This $null or empty scalar value should be returned by the function.  This is how values are
       added to Scalar hashes. */
   public Scalar getAt(Scalar key);

   /** Returns all of the keys within the scalar hash.  If a key has a $null (aka empty scalar) value the key should be
       removed from the scalar hash. */
   public ScalarArray keys();

   /** Removes the specified scalar from the hashmap. :) */
   public void remove(Scalar key);

   /** Return the data structure backing this hash please */
   public Map getData();
}
}