package sleep.taint;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import java.util.*;

public class Sanitizer implements Function, Operator
{
   protected Object function;

   public Sanitizer(Object f)
   {
      function = f;
   }

   public Scalar operate(String name, ScriptInstance script, Stack arguments)
   {
      Scalar value = ((Operator)function).operate(name, script, arguments);
      TaintUtils.untaint(value);
      return value;
   }

   public Scalar evaluate(String name, ScriptInstance script, Stack arguments)
   {
      Scalar value = ((Function)function).evaluate(name, script, arguments);
      TaintUtils.untaint(value);
      return value;
   }
}
