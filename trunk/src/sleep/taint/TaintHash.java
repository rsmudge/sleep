package sleep.taint;

import sleep.runtime.*;
import java.util.*;
import sleep.engine.ObjectUtilities;

/** This class is used to wrap read-only hashes */
public class TaintHash implements ScalarHash
{
   protected ScalarHash source;

   public TaintHash(ScalarHash src)
   {
      source = src;
   }

   public Scalar getAt(Scalar key)
   {
      return TaintUtils.taintAll(source.getAt(key));
   }

   /** this operation is kind of expensive... should be fixed up to take care of that */
   public ScalarArray keys()
   {
      return source.keys();
   }

   public void remove(Scalar key)
   {
      source.remove(key);
   }

   public Map getData()
   {
      Map temp = source.getData();

      Iterator i = temp.entrySet().iterator();
      while (i.hasNext())
      {
         Map.Entry next = (Map.Entry)i.next();

         if (next.getValue() != null && next.getKey() != null)
         {
            next.setValue(TaintUtils.taintAll((Scalar)next.getValue())); 
         }
      } 

      return temp;
   }

   public String toString()
   {
      return source.toString();
   }
}
