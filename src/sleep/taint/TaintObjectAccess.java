package sleep.taint;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

import sleep.bridges.SleepClosure;

import java.lang.reflect.*;

public class TaintObjectAccess extends PermeableStep
{
   protected String name;
   protected Class  classRef;

   public TaintObjectAccess(Step wrapit, String _name, Class _classRef)
   {
      super(wrapit);
      name     = _name;
      classRef = _classRef;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar scalar   = null;
      Scalar value    = null;

      if (classRef != null || SleepUtils.isFunctionScalar((Scalar)e.getCurrentFrame().peek()))
      {
         return super.evaluate(e);
      }

      String desc = e.hasFrame() ? TaintUtils.checkArguments(e.getCurrentFrame()) : null;

      scalar = (Scalar)e.getCurrentFrame().peek();

      if (desc != null && !TaintUtils.isTainted(scalar))
      {
         TaintUtils.taint(scalar);

         if ((e.getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_TRACE_TAINT) == ScriptInstance.DEBUG_TRACE_TAINT)
         {
            e.getScriptInstance().fireWarning("tainted object: " + SleepUtils.describe(scalar) + " from: " + desc, getLineNumber());
         }
      }

      return callit(e, desc);
   }
}
