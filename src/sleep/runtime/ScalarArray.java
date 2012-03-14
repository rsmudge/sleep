package sleep.runtime;

import java.util.*;

/**
 * <p>This interface lets you implement your own data structure behind a scalar
 * array.</p>
 * 
 * <p>To instantiate a custom scalar array:</p>
 * 
 * <code>Scalar temp = SleepUtils.getArrayScalar(new MyScalarArray());</code>
 * 
 * <p>When implementing the following interface, keep in mind you are implementing an
 * interface to an array data structure.</p>
 */
public interface ScalarArray extends java.io.Serializable
{
   /** remove the topmost element from the array */
   public Scalar   pop();

   /** add an element onto the end of the array */
   public Scalar   push(Scalar value);

   /** return the size of the array */
   public int      size();

   /** get an element at the specified index */
   public Scalar   getAt(int index);

   /** return an iterator */
   public Iterator scalarIterator();

   /** add an element to the array at the specified index */
   public Scalar   add(Scalar value, int index); 

   /** remove all elements with the same identity as the specified scalar */
   public void     remove(Scalar value);

   /** remove an element at the specified index */
   public Scalar   remove(int index);

   /** sort this array with the specified comparator */
   public void     sort(Comparator compare);

   /** return a view into the array, ideally one that uses the same backing store */
   public ScalarArray sublist(int start, int end);
}
