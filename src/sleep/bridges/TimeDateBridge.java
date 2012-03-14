package sleep.bridges;

import sleep.engine.*;
import sleep.runtime.*;
import sleep.interfaces.*;
import sleep.bridges.BridgeUtilities;

import java.text.*;
import java.util.*;

public class TimeDateBridge implements Loadable
{
   public void scriptLoaded(ScriptInstance script)
   {
      // time date functions 
      script.getScriptEnvironment().getEnvironment().put("&ticks",          new ticks());
      script.getScriptEnvironment().getEnvironment().put("&formatDate",     new formatDate());
      script.getScriptEnvironment().getEnvironment().put("&parseDate",      new parseDate());
   }

   public void scriptUnloaded(ScriptInstance script)
   {
   }

   private static class formatDate implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         long a = System.currentTimeMillis();

         if (locals.size() == 2)
            a = BridgeUtilities.getLong(locals);

         String b = locals.pop().toString();

         SimpleDateFormat format = new SimpleDateFormat(b);
         Date             adate  = new Date(a);

         return SleepUtils.getScalar(format.format(adate, new StringBuffer(), new FieldPosition(0)).toString());
      }
   }

   private static class parseDate implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         String a = locals.pop().toString();
         String b = locals.pop().toString();

         SimpleDateFormat format = new SimpleDateFormat(a);
         Date             pdate  = format.parse(b, new ParsePosition(0));

         return SleepUtils.getScalar(pdate.getTime());
      }
   }

   private static class ticks implements Function
   {
      public Scalar evaluate(String f, ScriptInstance si, Stack locals)
      {
         return SleepUtils.getScalar(System.currentTimeMillis());
      }
   }
}
