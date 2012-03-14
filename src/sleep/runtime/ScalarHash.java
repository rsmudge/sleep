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
public interface ScalarHash extends java.io.Serializable
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
