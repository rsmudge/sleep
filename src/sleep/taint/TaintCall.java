package sleep.taint;

import sleep.engine.*;
import sleep.engine.atoms.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import java.util.*;

public class TaintCall extends PermeableStep
{
   protected String function;

   public TaintCall(String _function, Step _wrapped)
   {
      super(_wrapped);
      function = _function;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      Function callme = e.getFunction(function);

      if (callme != null && (callme.getClass() == Tainter.class || callme.getClass() == Sanitizer.class))
      {
         return wrapped.evaluate(e);
      }
      else
      {
         return super.evaluate(e);
      }
   }
}
