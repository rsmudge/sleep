/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
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
