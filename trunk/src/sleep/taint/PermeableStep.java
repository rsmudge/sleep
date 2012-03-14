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
package sleep.taint;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import java.util.*;

public class PermeableStep extends Step
{
   protected Step wrapped;

   public PermeableStep(Step step)
   {
      wrapped = step;
   }

   public void setInfo(int _line)
   {
      wrapped.setInfo(_line);
   }

   public int getLineNumber()
   {
      return wrapped.getLineNumber();
   }

   public String toString(String prefix)
   {
      return prefix + "[Taint Wrap]\n" + wrapped.toString(prefix + "   ");
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      String desc = e.hasFrame() ? TaintUtils.checkArguments(e.getCurrentFrame()) : null;
      return callit(e, desc);
   }

   protected Scalar callit(ScriptEnvironment e, String desc)
   {
      wrapped.evaluate(e);

      if (desc != null && e.hasFrame() && !e.getCurrentFrame().isEmpty() && !SleepUtils.isEmptyScalar((Scalar)e.getCurrentFrame().peek()) && ((Scalar)e.getCurrentFrame().peek()).getActualValue() != null)
      {
         TaintUtils.taint((Scalar)e.getCurrentFrame().peek());

         if ((e.getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_TRACE_TAINT) == ScriptInstance.DEBUG_TRACE_TAINT)
         {
            e.getScriptInstance().fireWarning("tainted value: " + SleepUtils.describe((Scalar)e.getCurrentFrame().peek()) + " from: " + desc, getLineNumber());
         }

         return (Scalar)e.getCurrentFrame().peek();
      }

      return null;
   }
}
