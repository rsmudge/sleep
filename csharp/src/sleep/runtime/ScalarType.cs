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

using  java.io.Serializable;


namespace sleep.runtime{

/**
 * <p>This interface makes it possible to create a new scalar type.  A scalar type is responsible for being able to convert
 * itself to any type of scalar value.</p>
 * 
 * <p>To store a custom scalar type in a scalar:</p>
 * 
 * <pre>
 * Scalar temp = SleepUtils.getScalar(); // returns an empty scalar.
 * temp.setValue(new MyScalarType()); 
 * </pre>
 * 
 * <p>In the above example MyScalarType is an instance that implements the ScalarType interface.</p>
 * 
 */
 [Serializable]
public interface ScalarType : java.io.Serializable
{
   /** create a clone of this scalar's value.  It is important to note that you should return a copy here unless you really want 
       scalars of your scalar type to be passed by reference. */
   public ScalarType copyValue(); 

   /** convert the scalar to an int */
   public int        intValue();

   /** convert the scalar to a long */
   public long       longValue();

   /** convert the scalar to a double */
   public double     doubleValue();

   /** convert the scalar to a string */
   public String     toString();

   /** convert the scalar to an object value *shrug* */
   public Object     objectValue();

   /** returns the Class type of this ScalarType.  Use this instead of getClass to allow other functions to wrap ScalarType's without breaking
       functionality */
   public Type      getType();
}
}