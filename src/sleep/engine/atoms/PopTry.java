package sleep.engine.atoms;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

public class PopTry extends Step
{
   public PopTry ()
   {
   }

   public String toString(String prefix)
   {
      return prefix + "[Pop Try]\n";
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      e.popExceptionContext();
      return null;
   }
}



