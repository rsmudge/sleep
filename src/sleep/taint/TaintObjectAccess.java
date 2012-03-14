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
