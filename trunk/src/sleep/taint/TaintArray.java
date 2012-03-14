package sleep.taint;

import sleep.runtime.*;
import java.util.*;
import sleep.engine.ObjectUtilities;

/** Used to wrap read-only arrays so values are only converted on an as-needed basis */
public class TaintArray implements ScalarArray
{
   protected ScalarArray source;

   public ScalarArray sublist(int begin, int end)
   {
      return new TaintArray(source.sublist(begin, end));
   }

   public TaintArray(ScalarArray src)
   {
      source = src;
   }

   public String toString()
   {
      return source.toString();
   }

   public Scalar pop()
   {
      return TaintUtils.taintAll(source.pop());
   }

   public void sort(Comparator compare)
   {
      source.sort(compare);
   }

   public Scalar push(Scalar value)
   {
      return TaintUtils.taintAll(source.push(value));
   }

   public int size()
   {
      return source.size();
   }

   public Scalar remove(int index)
   {
      return TaintUtils.taintAll(source.remove(index));
   }

   public Scalar getAt(int index)
   {
      return TaintUtils.taintAll(source.getAt(index));
   }

   public Iterator scalarIterator()
   {
      return new TaintIterator(source.scalarIterator());
   }

   public Scalar add(Scalar value, int index)
   {
      return TaintUtils.taintAll(source.add(value, index));
   }

   public void remove(Scalar value)
   {
      source.remove(value);
   }

   protected class TaintIterator implements Iterator
   {
      protected Iterator realIterator;

      public TaintIterator(Iterator iter)
      {
         realIterator = iter;
      }

      public boolean hasNext()
      {
         return realIterator.hasNext(); 
      }

      public Object next()
      {
         return TaintUtils.taintAll((Scalar)realIterator.next());
      }

      public void remove()
      {
         realIterator.remove();
      }
   }
}
