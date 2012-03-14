package sleep.taint;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import java.util.*;

/** A sensitive function */
public class Sensitive implements Function
{
   protected Object function;

   public Sensitive(Object f)
   {      
      function = f;
   }   

   public Scalar evaluate(String name, ScriptInstance script, Stack arguments)
   {
      Stack dangers = new Stack();
      Iterator i = arguments.iterator();
      while (i.hasNext())
      {
         Scalar next = (Scalar)i.next();

         if (TaintUtils.isTainted(next))
         {
            dangers.push(next);
         }
      }

      if (dangers.isEmpty())
      {
         return ((Function)function).evaluate(name, script, arguments);
      }
      else
      {
         throw new RuntimeException("Insecure " + name + ": " + SleepUtils.describe(dangers) + " is tainted");
      }
   }
}
