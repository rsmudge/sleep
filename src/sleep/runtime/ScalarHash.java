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
