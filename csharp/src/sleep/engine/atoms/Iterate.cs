/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
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
using System;
using java = biz.ritter.javapi;

using  sleep.interfaces;
using  sleep.engine;
using  sleep.runtime;
using  sleep.bridges;

namespace sleep.engine.atoms{

public class Iterate : Step
{
   public class IteratorData
   {
      public String   key      = null;
      public Variable kenv     = null;

      public String   value    = null;
      public Variable venv     = null;

      public Scalar   source   = null;
      public java.util.Iterator<biz.ritter.javapi.util.MapNS.Entry<object, object>> iterator = null;
      public int      count    = 0;
   }

   public const int ITERATOR_CREATE   = 1;
   public const int ITERATOR_DESTROY  = 2;
   public const int ITERATOR_NEXT     = 3;

   public override String toString(String prefix)
   {
      switch (type)
      { 
         case Iterate.ITERATOR_CREATE:
            return prefix + "[Create Iterator]\n";
         case Iterate.ITERATOR_DESTROY:
            return prefix + "[Destroy Iterator]\n";
         case Iterate.ITERATOR_NEXT:
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
      java.util.Stack<Object> iterators = (java.util.Stack<Object>)(e.getContextMetadata("iterators"));
      iterators.pop();      
   }

   private void iterator_create(ScriptEnvironment e)
   {
      java.util.Stack<Object> temp = e.getCurrentFrame();
      
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
         data.iterator = new ProxyIterator((java.util.Iterator<Object>)data.source.objectValue(), true);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempted to use foreach on non-array: '" + data.source + "'", getLineNumber());
         data.iterator = null;
      }

      //
      // save the iterator
      //
      java.util.Stack<Object> iterators   = (java.util.Stack<Object>)(e.getContextMetadata("iterators"));

      if (iterators == null)
      {
         iterators = new java.util.Stack<Object>();
         e.setContextMetadata("iterators", iterators);
      }

      iterators.push(data);
   }

   private void iterator_next(ScriptEnvironment e)
   {
      java.util.Stack<Object> iterators   = (java.util.Stack<Object>)(e.getContextMetadata("iterators"));
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
      catch (java.util.ConcurrentModificationException cmex)
      {
         data.iterator = null; /* force a break out of the loop */
         throw (cmex);
      }

      if (data.source.getHash() != null)
      {
         if (SleepUtils.isEmptyScalar((Scalar)((java.util.MapNS.Entry<Object,Object>)next).getValue()))
         {
            e.getCurrentFrame().pop(); /* consume the old value true/false value */
            iterator_next(e);
            return;
         }

         if (data.key != null)
         {  
            data.kenv.putScalar(data.key, SleepUtils.getScalar(((java.util.MapNS.Entry<Object,Object>)next).getKey()));
            data.venv.putScalar(data.value, (Scalar)((java.util.MapNS.Entry<Object,Object>)next).getValue());
         }
         else
         {
            data.venv.putScalar(data.value, SleepUtils.getScalar(((java.util.MapNS.Entry<Object,Object>)next).getKey()));
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



}