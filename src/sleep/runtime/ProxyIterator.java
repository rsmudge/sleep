package sleep.runtime;

import java.util.*;
import sleep.engine.ObjectUtilities;

/* An iterator wrapper that constructs Sleep compatible Scalars for 
   each iterator. */
public class ProxyIterator implements Iterator
{
   protected Iterator realIterator;
   protected boolean  modifyAllow;

   public ProxyIterator(Iterator iter, boolean _modifyAllow)
   {
      realIterator = iter;
      modifyAllow  = _modifyAllow;
   }

   /** Check if the Scalar contains a Java iterator value */
   public static boolean isIterator(Scalar value)
   {
      return value.getActualValue() != null && value.objectValue() instanceof Iterator;
   }

   public boolean hasNext()
   {
      return realIterator.hasNext(); 
   }

   public Object next()
   {
      Object temp = realIterator.next();
      return ObjectUtilities.BuildScalar(true, temp);
   }

   public void remove()
   {
      if (modifyAllow)
      {
         realIterator.remove();
      }
      else
      {
         throw new RuntimeException("iterator is read-only");
      }
   }
}
