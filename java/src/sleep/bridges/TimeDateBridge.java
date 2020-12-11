/*
 * Copyright 2002-2020 Raphael Mudge
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
