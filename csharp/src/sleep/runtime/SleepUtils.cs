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

using  sleep.engine;
using  sleep.engine.types;
using  sleep.interfaces;
using  sleep.parser;
using  sleep.runtime;
using  sleep.bridges;

namespace sleep.runtime{
 

/** This class contains generalized utilities for instantiating/wrapping data into the sleep Scalar type. 
  * Included for free are methods for executing sleep blocks as well. 
  *
  * @see sleep.engine.Block
  * @see sleep.bridges.SleepClosure
  * @see sleep.runtime.Scalar
  * @see sleep.runtime.ScalarType
  * @see sleep.runtime.ScalarArray
  * @see sleep.runtime.ScalarHash
  */
public class SleepUtils
{
   /** A date stamp of this Sleep release in YYYYMMDD format */
   public const int    SLEEP_RELEASE = 20090430;

   /** A string description of this Sleep release */
   public const String SLEEP_VERSION = "Sleep 2.1";

   /** registers "keyword" as a keyword with the parser.  This is a necessity if you have extra non standard
     bridges in sleep */
   public static void addKeyword(String keyword)
   {
     Checkers.addKeyword(keyword);
   }

   /** Parses the specified code into a runnable block. */
   public static Block ParseCode(String code) //throws sleep.error.YourCodeSucksException
   {
       Parser parser = new Parser("eval", code);
       parser.parse();

       return parser.getRunnableBlock();
    }
  
    /** Iterates over the specified collection and removes all items that are the same as the specified scalar
        value.  Certain scalars (ints, doubles, etc.) are compared by string representation where as others 
        (Object, Hash, Array) are compared by reference. */
    public static void removeScalar(java.util.Iterator<Object> collection, Scalar value)
    {
        while (collection.hasNext())
        {
            Scalar next = (Scalar)collection.next();
            if (value.sameAs(next))
            {
               collection.remove();
            }
        }
     }

   /** utility function to handle the setup/teardown for a call request */
   private static Scalar runCode(CallRequest request, ScriptInstance script, java.util.Stack<Object> locals)
   {
       ScriptEnvironment environment = script.getScriptEnvironment();

       lock (environment.getScriptVariables())
       {
          environment.pushSource(script.getName());

          environment.CreateFrame(); /* this frame holds the result */
          environment.CreateFrame(locals); /* dump the local vars here plz */

          request.CallFunction();

          /* get the return value */   
          Scalar rv = environment.getCurrentFrame().isEmpty() ? SleepUtils.getEmptyScalar() : (Scalar)environment.getCurrentFrame().pop();

          /* handle the cleanup */
          environment.KillFrame();
          environment.popSource();
 
          /* necessary since we're doing this from a toplevel */
          environment.resetEnvironment();

          return rv;
       }
   }

   /** "safely" run a snippet of code.  The snippet is executed as if it was an inline function.
    *  @param code the block of code we want to execute
    *  @param env the environment to run the code in
    *  @return the scalar returned by the executed code (if their is a return value), null otherwise.
    */
   public static Scalar runCode(Block code, ScriptEnvironment env)
   {
       if (env.getScriptVariables().getLocalVariables() == null)
       {
          env.getScriptVariables().pushLocalLevel();

          CallRequest request = new CallRequest.InlineCallRequest(env, Integer.MIN_VALUE, "eval", code);
          Scalar value = runCode(request, env.getScriptInstance(), null);

          env.getScriptVariables().popLocalLevel();

          return value;
       }
       else
       {
          CallRequest request = new CallRequest.InlineCallRequest(env, Integer.MIN_VALUE, "eval", code);
          return runCode(request, env.getScriptInstance(), null);
       }
   }

   /** "safely" runs a closure.  
    *  @param closure the SleepClosure object we want to execute
    *  @param message the <var>$0</var> parameter (aka the message) to pass to this closure object
    *  @param script the script we want to execute the function within
    *  @param locals a stack of scalars representing the arguments to this Function (first arg on top)
    *  @return the scalar returned by the executed code or the sleep empty scalar if there is no return value (never returns null)
    */
   public static Scalar runCode(SleepClosure closure, String message, ScriptInstance script, java.util.Stack<Object> locals)
   {
       if (script == null)
       {
          script = closure.getOwner();
       }

       CallRequest request = new CallRequest.ClosureCallRequest(script.getScriptEnvironment(), Integer.MIN_VALUE, SleepUtils.getScalar(closure), message);
       return runCode(request, script, locals);
   }

   /** "safely" runs a "Function" of code.  
    *  @param func the Function object we want to execute
    *  @param name the name of the function we are executing (can be anything, depending on the function object)
    *  @param script the script we want to execute the function within
    *  @param locals a stack of scalars representing the arguments to this Function (first arg on top)
    *  @return the scalar returned by the executed code or the sleep empty scalar if there is no return value (never returns null)
    */
   public static Scalar runCode(Function func, String name, ScriptInstance script, java.util.Stack<Object> locals)
   {
       CallRequest request = new CallRequest.FunctionCallRequest(script.getScriptEnvironment(), Integer.MIN_VALUE, name, func);
       return runCode(request, script, locals);
   }
 
   /** "safely" run a snippet of code.  The main thing this function does is clear the return value 
    *  before returning the value to the caller.  This is important because the return value (if there 
    *  is one) would not get cleared otherwise.  Kind of important.
    *  @param script the owning script instance of this block of code
    *  @param code the block of code we want to execute
    *  @param vars a hashmap containing Scalar objects that should be installed into the local scope.  The keys should be Strings representing the $names for each of the Scalar variables. This value can be null.
    *  @return the scalar returned by the executed code (if their is a return value), null otherwise.
    */
   public static Scalar runCode(ScriptInstance script, Block code, java.util.HashMap<Object,Object> vars)
   {
       CallRequest request = new CallRequest.InlineCallRequest(script.getScriptEnvironment(), Integer.MIN_VALUE, "eval", code);
       return runCode(request, script, getArgumentStack(vars));
   }

   /** "safely" run a snippet of code.  The main thing this function does is clear the return value 
    *  before returning the value to the caller.  This is important because the return value (if there 
    *  is one) would not get cleared otherwise.  Kind of important.
    *  @param owner the owning script instance of this block of code
    *  @param code the block of code we want to execute
    *  @return the scalar returned by the executed code (if their is a return value), null otherwise.
    */
   public static Scalar runCode(ScriptInstance owner, Block code)
   {
       return runCode(owner, code, null);
   }

   /** An easy way to make your programs data structures available in the script environment.  Using this wrapper method will 
       make the contents of your Set data structure available in a read only way to scripters using your program.  
       Values your data structure returns are turned into Scalar strings using the toString() method of the object.   If you 
       need something more specific than this then take a look at the source code for sleep.runtime.SetWrapper which implements 
       sleep.runtime.ScalarArray.  */
   public static Scalar getArrayWrapper(java.util.Collection<Object> dataStructure)
   {
      Scalar temp = new Scalar();
      temp.setValue(new CollectionWrapper(dataStructure));
     
      return temp;
   }

   /** An easy way to make your programs data structures available in the script environment.  Using this wrapper method will 
       make the contents of your Map'd data structure available in a read only way to scripters using your program.   Keys are  
       automatically turned into strings and values your data structure give back are turned into Scalar strings using the 
       toString() method of the object.   If you need something more specific than this then take a look at the source code for
       sleep.runtime.MapWrapper which implements sleep.runtime.ScalarHash.  */
   public static Scalar getHashWrapper(java.util.Map<Object,Object> dataStructure)
   {
      Scalar temp = new Scalar();
      temp.setValue(new MapWrapper(dataStructure));
     
      return temp;
   }

   /** Creates a scalar with the specified ScalarHash as the value */
   public static Scalar getHashScalar(ScalarHash value)
   {
      Scalar temp = new Scalar();
      temp.setValue(value);
     
      return temp;
   }

   /** Creates a scalar with the specified ScalarArray as the value */
   public static Scalar getArrayScalar(ScalarArray value)
   {
      Scalar temp = new Scalar();
      temp.setValue(value);
     
      return temp;
   }

   /** returns a Scalar variable containing a SLEEP array as its value (everything in SLEEP is stored as a Scalar */
   public static Scalar getArrayScalar()
   {
      Scalar temp = new Scalar();
      temp.setValue(new ListContainer());

      return temp;
   }

   /** Generate a java.util.Map from a scalar hash.  Keys will be Java strings.  Values will be
       the Java object equivalents of the data stored in the scalar hash. */
   public static java.util.Map<Object,Object> getMapFromHash(Scalar map)
   { 
      return getMapFromHash(map.getHash());
   }

   /** Generate a java.util.Map from a scalar hash.  Keys will be Java strings.  Values will be
       the Java object equivalents of the data stored in the scalar hash. */
   public static java.util.Map<Object,Object> getMapFromHash(ScalarHash map)
   {
      java.util.HashMap dict = new java.util.HashMap();

      if (map != null)
      {
         java.util.Iterator i = map.keys().scalarIterator();
         while (i.hasNext())
         {
            Scalar key  = (Scalar)i.next();
            Scalar val  = map.getAt(key);

            if (val.getHash() != null)
            {
               dict.put(key.toString(), getMapFromHash(val.getHash()));
            }
            else if (val.getArray() != null)
            {
               dict.put(key.toString(), getListFromArray(val.getArray()));
            }
            else
            {  
               dict.put(key.toString(), val.objectValue());
            }
         }
      }

      return dict;
   }

   /** Generate a java.util.Stack<Object> of sleep.bridges.KeyValuePair arguments from a Map.  Assumes the keys are Strings and the values are already Scalar values. */
   public static java.util.Stack<Object> getArgumentStack(java.util.Map<Object,Object> pairs)
   {
       java.util.Stack<Object> locals = new java.util.Stack<Object>();

       /* turn our hashmap into some acceptable local variables */
       if (pairs != null)
       {
          Iterator i = pairs.entrySet().iterator();
          while (i.hasNext())
          {
             java.util.Map.Entry value = (java.util.Map.Entry)i.next();
             locals.push(SleepUtils.getScalar(new KeyValuePair(SleepUtils.getScalar(value.getKey().toString()), (Scalar)value.getValue())));
          }
       }

       return locals;
   }

   /** Returns a scalar iterator depending grabbed from the Scalar.  The scalar can contain an array, a function, or a java.util.Iterator object. */
   public static java.util.Iterator<Object> getIterator(Scalar temp, ScriptInstance script)
   {
      if (temp.getArray() != null)
      {
         return temp.getArray().scalarIterator();
      }
      else if (SleepUtils.isFunctionScalar(temp))
      {
         return SleepUtils.getFunctionFromScalar(temp).scalarIterator();
      }   
      else if (ProxyIterator.isIterator(temp))
      {
         return new ProxyIterator((Iterator)temp.objectValue(), true);
      }

      throw new IllegalArgumentException("expected iterator (@array or &closure)--received: " + SleepUtils.describe(temp));
   }

   /** Generate a java.util.List from a scalar array.  Values will be the Java object 
       equivalents of the data stored in the scalar array. */
   public static java.util.List<Object> getListFromArray(Scalar array)
   {
      return getListFromArray(array.getArray());
   }

   /** Generate a java.util.List from a scalar array.  Values will be the Java object 
       equivalents of the data stored in the scalar array. */
   public static java.util.List<Object> getListFromArray(ScalarArray array)
   {
      LinkedList list = new LinkedList();
       
      if (array != null)
      {
         Iterator i = array.scalarIterator();
         while (i.hasNext())
         {
            Scalar temp = (Scalar)i.next();

            if (temp.getHash() != null)
            {
               list.add(getMapFromHash(temp.getHash()));
            }
            else if (temp.getArray() != null)
            {
               list.add(getListFromArray(temp.getArray()));
            }
            else
            {
               list.add(temp.objectValue());
            }
         }
      }

      return list;
   }

   /** a shared instance of the dreaded null scalar... */
   protected static ScalarType nullScalar = new NullValue();

   /** returns the null scalar, which will always be equal to 0, "", and null simultaneously. The instance of the null scalar is
       shared since the null scalar "value" is not modifiable.  */
   public static Scalar getEmptyScalar()
   {
      Scalar temp = new Scalar();
      temp.setValue(nullScalar);

      return temp;
   }

   /** returns true if the passed in scalar value is equivalent to the empty scalar or null */
   public static bool isEmptyScalar(Scalar value)
   {
      return (value == null || value.getActualValue() == nullScalar);
   }

   /** Determines if the passed in scalar represents a "function" value.  True iff the scalar contains a closure reference. */
   public static bool isFunctionScalar(Scalar value)
   {
      return (value.objectValue() != null && value.objectValue() is SleepClosure);
   }

   /** extracts a callable Function from the passed in Scalar.  Returns null if value does not contain a function.  Calling isFunctionScalar before this method is highly recommended. */
   public static SleepClosure getFunctionFromScalar(Scalar value)
   {
       if (value.objectValue() != null && value.objectValue() is SleepClosure)
           return (SleepClosure)value.objectValue();

       return null;
   }

   /** extracts a callable Function from the passed in Scalar.  The passed in Scalar can be either a SleepClosure scalar or a string scalar specifying a function name.
       This method exists for backwards compatability of old jIRCii scripts. */
   public static SleepClosure getFunctionFromScalar(Scalar value, ScriptInstance script)
   {
      if (value.objectValue() != null && value.objectValue() is SleepClosure)
          return (SleepClosure)value.objectValue();

      return (SleepClosure)script.getScriptEnvironment().getFunction(value.toString());
   }

   /** creates an IO handle scalar suitable for use with the sleep IO API.  The passed in
       streams can each be null if necessary. */ 
   public static Scalar getIOHandleScalar(java.io.InputStream inJ, java.io.OutputStream outJ)
   {
      return SleepUtils.getScalar(getIOHandle(inJ, outJ));
   }

   /** creates an IO handle scalar suitable for use with the sleep IO API.  The passed in
       streams can each be null if necessary. */ 
   public static sleep.bridges.io.IOObject getIOHandle(java.io.InputStream inJ, java.io.OutputStream outJ)
   {
      sleep.bridges.io.IOObject handle = new sleep.bridges.io.IOObject();
      handle.openRead(inJ);
      handle.openWrite(outJ);

      return handle;
   }

   /** Creates a proxy instance of the specified class (limited to interfaces at this time) that is backed with the specified closure */
   public static Object newInstance(Type initializeMe, SleepClosure closure, ScriptInstance owner)
   {
      return ProxyInterface.BuildInterface(initializeMe, closure, owner != null ? owner : closure.getOwner());
   }

   /** Creates a proxy instance of the specified class (limited to interfaces at this time) that is backed with the specified block of code (made into a closure) */
   public static Object newInstance(Type initializeMe, Block code, ScriptInstance owner)
   {
      return ProxyInterface.BuildInterface(initializeMe, new SleepClosure(owner, code), owner);
   }
  
   /** returns a comma separated list of descriptions of the scalars in the specified argument
       stack.  This is used by the debugging mechanism to format arguments to strings based on
       their scalar type. */
   public static String describe(java.util.Stack<Object> arguments)
   {
      StringBuffer values = new StringBuffer();

      Iterator i = arguments.iterator();
      while (i.hasNext())
      {
         Scalar tempz = (Scalar)i.next();

         values.insert(0, SleepUtils.describe(tempz));

         if (i.hasNext()) { values.insert(0, ", "); }
      }

      return values.toString();
   }

   private static String describeEntries(java.util.List<Object> seen, Scalar scalar)
   {
      if (scalar.getArray() != null)
      {
         if (seen.contains(scalar.getArray()))
         {
            return "@" + seen.indexOf(scalar.getArray());
         }
         else
         {
            seen.add(scalar.getArray());

            StringBuffer buffer = new StringBuffer("@(");

            Iterator i = scalar.getArray().scalarIterator();
            while (i.hasNext())
            {
               Scalar next = (Scalar)i.next();
               buffer.append(describeEntries(seen, next));

               if (i.hasNext())
               {
                  buffer.append(", ");
               }
            }

            buffer.append(")");
            return buffer.toString();
         }
      }
      else if (scalar.getHash() != null)
      {
         if (seen.contains(scalar.getHash()))
         {
            return "%" + seen.indexOf(scalar.getHash());
         }
         else
         {
            seen.add(scalar.getHash());

            StringBuffer buffer = new StringBuffer("%(");

            Iterator i = scalar.getHash().getData().entrySet().iterator();
            while (i.hasNext())
            {
               Map.Entry next = (Map.Entry)i.next();
               Scalar value   = (Scalar)next.getValue();

               if (!SleepUtils.isEmptyScalar((Scalar)next.getValue()))
               {
                  if (buffer.length() > 2)
                  {
                     buffer.append(", ");
                  }

                  buffer.append(next.getKey());
                  buffer.append(" => ");
  
                  buffer.append(describeEntries(seen, value));
               } 
            }

            buffer.append(")");
            return buffer.toString();
         }
      }
      else
      {
         if (scalar.getActualValue().getType() == typeof(NullValue))
         {
            return "$null";
         }
         else if (scalar.getActualValue().getType() == typeof(StringValue))
         {
            return "'" + scalar.toString() + "'";
         }
         else if (isFunctionScalar(scalar))
         {
            return scalar.toString();
         }
         else if (scalar.objectValue() is KeyValuePair)
         {
            KeyValuePair kvp = (KeyValuePair)scalar.objectValue();
            return kvp.getKey().toString() + " => " + describe(kvp.getValue());
         }
         else if (scalar.getActualValue().getType() == typeof(ObjectValue))
         {
            if (java.lang.reflect.Proxy.isProxyClass(scalar.objectValue().getClass()))
            {
               StringBuffer buffer = new StringBuffer();
               buffer.append("[");
               buffer.append(java.lang.reflect.Proxy.getInvocationHandler(scalar.objectValue()).toString());
               buffer.append(" as ");

               Class[] interfaces = scalar.objectValue().getClass().getInterfaces();

               for (int x = 0; x < interfaces.length; x++)
               {
                  if (x > 0)
                  {
                     buffer.append(", "); 
                  }
                  buffer.append(interfaces[x].getName());
               }

               buffer.append("]");

               return buffer.toString();
            }
            return scalar.toString();
         }
         else if (scalar.getActualValue().getType() == typeof(LongValue))
         {
            return scalar.toString() + "L";
         }
         else
         {
            return scalar.toString();
         }
      }
   }

   /** returns a string description of the specified scalar. Used by debugging mechanism to
       format scalars based on their value type, i.e. strings are enclosed in single quotes,
       objects in brackets, $null is displayed as $null, etc. */
   public static String describe(Scalar scalar)
   {
      return describeEntries(new LinkedList(), scalar);
   }

   /** returns an empty hashmap scalar */
   public static Scalar getHashScalar()
   {
      Scalar temp = new Scalar();
      temp.setValue(new HashContainer());

      return temp;
   }

   /** returns an empty insertion ordered hashmap scalar */
   public static Scalar getOrderedHashScalar()
   {
      Scalar temp = new Scalar();
      temp.setValue(new OrderedHashContainer(11, 0.75f, false));

      return temp;
   }

   /** returns an empty access ordered hashmap scalar */
   public static Scalar getAccessOrderedHashScalar()
   {
      Scalar temp = new Scalar();
      temp.setValue(new OrderedHashContainer(11, 0.75f, true));

      return temp;
   }

   /** returns an int scalar with value x */
   public static Scalar getScalar(int x)
   {
      Scalar temp = new Scalar();
      temp.setValue(new IntValue(x));

      return temp;
   } 

   /** returns an int scalar (coverted from the short) with value x */
   public static Scalar getScalar(short x)
   {
      Scalar temp = new Scalar();
      temp.setValue(new IntValue((int)x));

      return temp;
   } 

   /** returns a double scalar (coverted from the float) with value x */
   public static Scalar getScalar(float x)
   {
      Scalar temp = new Scalar();
      temp.setValue(new DoubleValue((double)x));

      return temp;
   } 

   /** returns a double scalar with value x */
   public static Scalar getScalar(double x)
   {
      Scalar temp = new Scalar();
      temp.setValue(new DoubleValue(x));

      return temp;
   }

   /** Forces a copy of the value of the passed in scalar to be made.  Sleep scalars in general are passed by *value*.  When 
       passing a scalar, a new scalar should be made with a copy of the old scalars value.  Object scalars are passed by 
       reference but this copying mechanism handles that.  If you are ever storing scalars in a data structure call this method to 
       get a copy.  Otherwise chaos might ensue. */
   public static Scalar getScalar(Scalar x)
   {
      Scalar temp = new Scalar();
      temp.setValue(x);
    
      return temp;
   }

   /** returns a long scalar with value x */
   public static Scalar getScalar(long x)
   {
      Scalar temp = new Scalar();
      temp.setValue(new LongValue(x));

      return temp;
   }

   /** constructs a string scalar with value x interpreted as an array of unsigned bytes */
   public static Scalar getScalar(byte[] x)
   {
      return getScalar(x, x.length);
   }

   /** constructs a string scalar with value x interpreted as an array of unsigned bytes */
   public static Scalar getScalar(byte[] x, int length)
   {
      Scalar temp = new Scalar();
      StringBuffer buff = new StringBuffer(length);
      for (int y = 0; y < length; y++)
      {
         char append = (char)(x[y] & 0xFF);
         buff.append(append);
      }

      temp.setValue(new StringValue(buff.toString()));
 
      return temp;
   }

   /** returns a string scalar with value x */
   public static Scalar getScalar(String x)
   {
      if (x == null)
      {
         return SleepUtils.getEmptyScalar();
      }

      Scalar temp = new Scalar();
      temp.setValue(new StringValue(x));

      return temp;
   }

   /** returns an object scalar with value x */
   public static Scalar getScalar(Object x)
   {
      if (x == null)
      {
         return SleepUtils.getEmptyScalar();
      }

      Scalar temp = new Scalar();
      temp.setValue(new ObjectValue(x));

      return temp;
   }

   /** if x is true, the value will be 1, if x is false the value will be the empty scalar */
   public static Scalar getScalar(bool x)
   {
      if (x)
      {
         return SleepUtils.getScalar(1); // thanks to Ralph Becker for finding my lack of a return statement here :)
      }

      return SleepUtils.getEmptyScalar();
   }

   /** check if the scalar is true using Sleep's definition of truth.  A scalar is considered true if it is not $null and it is not equal to a representation of 0 */
   public static bool isTrueScalar(Scalar value)
   {
      return (value.getArray() != null || value.getHash() != null) || (value.getActualValue().toString().length() != 0 && !("0".equals(value.getActualValue().toString())));
   }
}
}