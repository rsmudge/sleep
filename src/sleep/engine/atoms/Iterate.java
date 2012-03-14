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
package sleep.engine.atoms;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

import sleep.bridges.SleepClosure;

public class Iterate extends Step
{
   public static class IteratorData
   {
      public String   key      = null;
      public Variable kenv     = null;

      public String   value    = null;
      public Variable venv     = null;

      public Scalar   source   = null;
      public Iterator iterator = null;
      public int      count    = 0;
   }

   public static final int ITERATOR_CREATE   = 1;
   public static final int ITERATOR_DESTROY  = 2;
   public static final int ITERATOR_NEXT     = 3;

   public String toString(String prefix)
   {
      switch (type)
      { 
         case ITERATOR_CREATE:
            return prefix + "[Create Iterator]\n";
         case ITERATOR_DESTROY:
            return prefix + "[Destroy Iterator]\n";
         case ITERATOR_NEXT:
            return prefix + "[Iterator next]\n";
      }

      return prefix + "[Iterator Unknown!@]";
   }

   protected int    type = 0;
   protected String key;
   protected String value;

   public Iterate(String _key, String _value, int _type)
   {
      type  = _type;
      key   = _key;
      value = _value;
   }

   private void iterator_destroy(ScriptEnvironment e)
   {
      Stack iterators = (Stack)(e.getContextMetadata("iterators"));
      iterators.pop();      
   }

   private void iterator_create(ScriptEnvironment e)
   {
      Stack temp = e.getCurrentFrame();
      
      //
      // grab our values off of the current frame...
      //
      IteratorData data = new IteratorData();
      data.source   = (Scalar)(temp.pop());
      e.KillFrame();

      //
      // setup our variables :)
      //
      data.value = value;
      data.venv  = e.getScriptVariables().getScalarLevel(value, e.getScriptInstance());

      if (data.venv == null)
      {
         data.venv = e.getScriptVariables().getGlobalVariables();

         if ((e.getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_REQUIRE_STRICT) == ScriptInstance.DEBUG_REQUIRE_STRICT)
         {
            e.showDebugMessage("variable '" + data.value + "' not declared");
         }
      }

      if (key != null)
      {
         data.key  = key;
         data.kenv = e.getScriptVariables().getScalarLevel(key, e.getScriptInstance());

         if (data.kenv == null)
         {
            data.kenv = e.getScriptVariables().getGlobalVariables();

            if ((e.getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_REQUIRE_STRICT) == ScriptInstance.DEBUG_REQUIRE_STRICT)
            {
               e.showDebugMessage("variable '" + data.key + "' not declared");
            }
         }
      }
      
      //
      // setup the iterator
      //
      if (data.source.getHash() != null)
      {
         data.iterator = data.source.getHash().getData().entrySet().iterator();
      }
      else if (data.source.getArray() != null)
      {
         data.iterator = data.source.getArray().scalarIterator();
      }
      else if (SleepUtils.isFunctionScalar(data.source))
      {
         data.iterator = SleepUtils.getFunctionFromScalar(data.source, e.getScriptInstance()).scalarIterator();
      }
      else if (ProxyIterator.isIterator(data.source))
      {
         data.iterator = new ProxyIterator((Iterator)data.source.objectValue(), true);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempted to use foreach on non-array: '" + data.source + "'", getLineNumber());
         data.iterator = null;
      }

      //
      // save the iterator
      //
      Stack iterators   = (Stack)(e.getContextMetadata("iterators"));

      if (iterators == null)
      {
         iterators = new Stack();
         e.setContextMetadata("iterators", iterators);
      }

      iterators.push(data);
   }

   private void iterator_next(ScriptEnvironment e)
   {
      Stack iterators   = (Stack)(e.getContextMetadata("iterators"));
      IteratorData data = (IteratorData)(iterators.peek());

      if (data.iterator != null && data.iterator.hasNext())
      {
         e.getCurrentFrame().push(SleepUtils.getScalar(true));
      }
      else
      {
         e.getCurrentFrame().push(SleepUtils.getScalar(false));
         return;
      }
     
      Object next = null;
      try
      {
         next = data.iterator.next();
      }
      catch (ConcurrentModificationException cmex)
      {
         data.iterator = null; /* force a break out of the loop */
         throw (cmex);
      }

      if (data.source.getHash() != null)
      {
         if (SleepUtils.isEmptyScalar((Scalar)((Map.Entry)next).getValue()))
         {
            e.getCurrentFrame().pop(); /* consume the old value true/false value */
            iterator_next(e);
            return;
         }

         if (data.key != null)
         {  
            data.kenv.putScalar(data.key, SleepUtils.getScalar(((Map.Entry)next).getKey()));
            data.venv.putScalar(data.value, (Scalar)((Map.Entry)next).getValue());
         }
         else
         {
            data.venv.putScalar(data.value, SleepUtils.getScalar(((Map.Entry)next).getKey()));
         }
      }
      else
      {
         if (data.key != null)
         {
            data.kenv.putScalar(data.key, SleepUtils.getScalar(data.count));
            data.venv.putScalar(data.value, (Scalar)next);
         }
         else
         {
            data.venv.putScalar(data.value, (Scalar)next);
         }
      }

      data.count++;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      if (type == ITERATOR_NEXT)
      {
         iterator_next(e);
      }
      else if (type == ITERATOR_CREATE)
      {
         iterator_create(e);
      }
      else if (type == ITERATOR_DESTROY)
      {
         iterator_destroy(e);
      }

      return null;
   }
}



