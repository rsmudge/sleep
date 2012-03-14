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
