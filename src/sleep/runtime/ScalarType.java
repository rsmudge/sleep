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

import java.io.Serializable;

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
public interface ScalarType extends java.io.Serializable
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
   public Class      getType();
}
