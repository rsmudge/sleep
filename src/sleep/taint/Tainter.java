package sleep.taint;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import java.util.*;

public class Tainter implements Function
{
   protected Object function;

   public Tainter(Object f)
   {
      function = f;
   }

   public Scalar evaluate(String name, ScriptInstance script, Stack arguments)
   {
      Scalar value = ((Function)function).evaluate(name, script, arguments);
      TaintUtils.taintAll(value);
      return value;
   }
}
