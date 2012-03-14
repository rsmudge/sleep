package sleep.taint;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

public class TaintOperate extends PermeableStep
{
   String oper;

   public TaintOperate(String o, Step _wrapped)
   {
       super(_wrapped);
       oper = o;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      Operator callme = e.getOperator(oper);

      if (callme != null && callme.getClass() == Sanitizer.class)
      {
         wrapped.evaluate(e);
      }
      else
      {
         super.evaluate(e);
      }

      return null;
   }
}



