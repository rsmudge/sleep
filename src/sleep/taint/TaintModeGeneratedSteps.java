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
import sleep.engine.types.*;
import sleep.engine.atoms.*;

import sleep.runtime.*;
import sleep.interfaces.*;

import java.util.*;

/** A replacement factory that generates Sleep interpreter instructions that honor and spread the taint mode. */
public class TaintModeGeneratedSteps extends GeneratedSteps
{
   public Step Call(String function)
   {
      return new TaintCall(function, super.Call(function));
   }

   public Step PLiteral(List doit)
   {
      return new PermeableStep(super.PLiteral(doit));
   }

   public Step Operate(String oper)
   {
      return new TaintOperate(oper, super.Operate(oper));
   }

   public Step ObjectNew(Class name)
   {
      return new PermeableStep(super.ObjectNew(name));
   }

   public Step ObjectAccess(String name)     
   {
      return new TaintObjectAccess(super.ObjectAccess(name), name, null);
   }

   public Step ObjectAccessStatic(Class aClass, String name)
   {
      return new TaintObjectAccess(super.ObjectAccessStatic(aClass, name), name, aClass);
   }
}
