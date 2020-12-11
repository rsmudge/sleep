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

using  sleep.runtime;
using  sleep.engine.types;
using  sleep.interfaces;
using  sleep.bridges;

namespace sleep.engine{

/** This class is sort of the center of the HOES universe containing several methods for mapping 
    between Sleep and Java and resolving which mappings make sense. */
public class ObjectUtilities
{
   private static Type STRING_SCALAR;
   private static Type INT_SCALAR; 
   private static Type DOUBLE_SCALAR;
   private static Type LONG_SCALAR;
   private static Type OBJECT_SCALAR;

   static ObjectUtilities()
   {
      STRING_SCALAR = typeof(sleep.engine.types.StringValue);
      INT_SCALAR    = typeof(sleep.engine.types.IntValue);
      DOUBLE_SCALAR = typeof(sleep.engine.types.DoubleValue);
      LONG_SCALAR   = typeof(sleep.engine.types.LongValue);
      OBJECT_SCALAR = typeof(sleep.engine.types.ObjectValue);
   }

   /** when looking for a Java method that matches the sleep args, we use a Yes match immediately */
   public static readonly int ARG_MATCH_YES   = 3;
  
   /** when looking for a Java method that matches the sleep args, we immediately drop all of the no answers. */
   public static readonly int ARG_MATCH_NO    = 0;

   /** when looking for a Java method that matches the sleep args, we save the maybes and use them as a last resort if no yes match is found */
   public static readonly int ARG_MATCH_MAYBE = 1;

   /** convienence method to determine wether or not the stack of values is a safe match for the specified method signature */
   public static int isArgMatch(Type[] check, java.util.Stack<Object> arguments)
   {
      int value = ARG_MATCH_YES;

      for (int z = 0; z < check.Length; z++)
      {
         Scalar scalar = (Scalar)arguments.get(check.Length - z - 1);

         value = value & isArgMatch(check[z], scalar);

//         System.out.println("Matching: " + scalar + "(" + scalar.getValue().getClass() + "): to " + check[z] + ": " + value);
 
         if (value == ARG_MATCH_NO)
         {
            return ARG_MATCH_NO;
         }
      }

      return value;
   }

   /** converts the primitive version of the specified class to a regular usable version */
   private static Type normalizePrimitive(Type check)
   {
      if (check == java.lang.Integer.TYPE) { check = typeof(java.lang.Integer); }
      else if (check == java.lang.Double.TYPE)   { check = typeof(java.lang.Double); }
      else if (check == java.lang.Long.TYPE)     { check = typeof(java.lang.Long); }
      else if (check == java.lang.Float.TYPE)    { check = typeof(java.lang.Float); }
      else if (check == java.lang.Boolean.TYPE)  { check = typeof(java.lang.Boolean); }
      else if (check == java.lang.Byte.TYPE)     { check = typeof(java.lang.Byte); }
      else if (check == java.lang.Character.TYPE) { check = typeof(java.lang.Character); }
      else if (check == java.lang.Short.TYPE)    { check = typeof(java.lang.Short); }

      return check;
   }

   /** determined if the specified scalar can be rightfully cast to the specified class */
   public static int isArgMatch(Type check, Scalar scalar)
   {
      if (SleepUtils.isEmptyScalar(scalar))
      {
         return ARG_MATCH_YES;
      }
      else if (scalar.getArray() != null)
      {
         if (check.isArray())
         {
            Type compType = check.getComponentType(); /* find the actual nuts and bolts component type so we can work with it */
            while (compType.isArray())
            {
               compType = compType.getComponentType();
            }

            Type mytype = getArrayType(scalar, null);
 
            if (mytype != null && compType.isAssignableFrom(mytype))
            {
               return ARG_MATCH_YES;
            }
            else
            {
               return ARG_MATCH_NO;
            }
         }
         else if (check == typeof(java.util.List<Object>) || check == typeof(java.util.Collection<Object>))
         {
            // would a java.util.List or java.util.Collection satisfy the argument?
            return ARG_MATCH_YES;
         }
         else if (check == typeof(ScalarArray))
         {
            return ARG_MATCH_YES;
         }
         else if (check == typeof (System.Object))
         {
            return ARG_MATCH_MAYBE;
         }
         else
         {
            return ARG_MATCH_NO;
         }
      }
      else if (scalar.getHash() != null)
      {
         if (check == typeof(java.util.Map<Object,Object>))
         {
            // would a java.util.Map or java.util.Collection satisfy the argument?
            return ARG_MATCH_YES;
         }
         else if (check == typeof(ScalarHash))
         {
            return ARG_MATCH_YES;
         }
         else if (check == typeof(System.Object))
         {
            return ARG_MATCH_MAYBE;
         }
         else
         {
            return ARG_MATCH_NO;
         }
      }
      else if (check.isPrimitive())
      {
         Type stemp = scalar.getActualValue().getType(); /* at this point we know scalar is not null, not a hash, and not an array */

         if (stemp == INT_SCALAR && check == java.lang.Integer.TYPE)
         {
            return ARG_MATCH_YES;
         }
         else if (stemp == DOUBLE_SCALAR && check == java.lang.Double.TYPE)
         {
            return ARG_MATCH_YES;
         }
         else if (stemp == LONG_SCALAR && check == java.lang.Long.TYPE)
         {
            return ARG_MATCH_YES;
         }
         else if (check == java.lang.Character.TYPE && stemp == STRING_SCALAR && scalar.getActualValue().toString().length() == 1)
         {
            return ARG_MATCH_YES;
         }
         else if (stemp == OBJECT_SCALAR)
         {
            check = normalizePrimitive(check);
            return (scalar.objectValue().getClass() is check) ? ARG_MATCH_YES : ARG_MATCH_NO;
         }
         else
         {
            /* this is my lazy way of saying allow Long, Int, and Double scalar types to be considered
               maybes... */
            return (stemp == STRING_SCALAR) ? ARG_MATCH_NO : ARG_MATCH_MAYBE;
         }
      }
      else if (check.isInterface())
      {
         if (SleepUtils.isFunctionScalar(scalar) || check.isInstance(scalar.objectValue()))
         {
            return ARG_MATCH_YES;
         }
         else
         {
            return ARG_MATCH_NO;
         }
      }
      else if (check == typeof(java.lang.StringJ) || check == typeof (System.String))
      {
         Type stemp = scalar.getActualValue().getType();
         return (stemp == STRING_SCALAR) ? ARG_MATCH_YES : ARG_MATCH_MAYBE;
      }
      else if (check == typeof(System.Object))
      {
         return ARG_MATCH_MAYBE; /* we're vying for anything and this will match anything */
      }
      else if (check.isInstance(scalar.objectValue()))
      {
         Type stemp = scalar.getActualValue().getType();
         return (stemp == OBJECT_SCALAR) ? ARG_MATCH_YES : ARG_MATCH_MAYBE;
      }
      else if (check.isArray())
      {
         Type stemp = scalar.getActualValue().getType();
         if (stemp == STRING_SCALAR && (check.getComponentType() == java.lang.Character.TYPE || check.getComponentType() == Byte.TYPE))
         {
            return ARG_MATCH_MAYBE;
         }
         else
         {
            return ARG_MATCH_NO;
         }
      }
      else
      {
         return ARG_MATCH_NO;
      }
   }

   /** attempts to find the method that is the closest match to the specified arguments */
   public static java.lang.reflect.Method findMethod(Type theClass, String method, java.util.Stack<Object> arguments)
   {
      int      size    = arguments.size();

      java.lang.reflect.Method   temp    = null;
      java.lang.reflect.Method[] methods = theClass.getMethods();

      for (int x = 0; x < methods.Length; x++) 
      {
         if (methods[x].getName().equals(method) && methods[x].getParameterTypes().Length == size)
         {
             if (size == 0)
                   return methods[x];

             int value = isArgMatch(methods[x].getParameterTypes(), arguments);
             if (value == ARG_MATCH_YES) 
                   return methods[x];

             if (value == ARG_MATCH_MAYBE)
                   temp = methods[x];
         }
      }

      return temp;
   }

   /** attempts to find the constructor that is the closest match to the arguments */
   public static java.lang.reflect.Constructor findConstructor(Type theClass, java.util.Stack<Object> arguments)
   {
      int      size    = arguments.size();

      Constructor   temp         = null;
      Constructor[] constructors = theClass.getConstructors();

      for (int x = 0; x < constructors.Length; x++) 
      {
         if (constructors[x].getParameterTypes().length == size)
         {
             if (size == 0)
                   return constructors[x];

             int value = isArgMatch(constructors[x].getParameterTypes(), arguments);
             if (value == ARG_MATCH_YES)
                   return constructors[x];

             if (value == ARG_MATCH_MAYBE)
                   temp = constructors[x];
         }
      }

      return temp;
   }

   /** this function checks if the specified scalar is a Class literal and uses that if it is, otherwise description is converted to a string and the convertDescriptionToClass method is used */
   public static Type convertScalarDescriptionToClass(Scalar description)
   {
       if (description.objectValue() is Type)
       {
          return (Type)description.objectValue();
       }

       return convertDescriptionToClass(description.toString());
   }

   /** converts the one character class description to the specified Class type, i.e. z = boolean, c = char, b = byte, i = integer, etc.. */
   public static Type convertDescriptionToClass(String description)
   {
      if (description.length() != 1)
      {
         return null;
      }

      Type atype = null;

      switch (description.charAt(0))
      {
         case 'z':
            atype = java.lang.Boolean.TYPE;
            break;
         case 'c':
            atype = java.lang.Character.TYPE;
            break;
         case 'b':
            atype = java.lang.Byte.TYPE;
            break;
         case 'h':
            atype = java.lang.Short.TYPE;
            break;
         case 'i':
            atype = java.lang.Integer.TYPE;
            break;
         case 'l':
            atype = java.lang.Long.TYPE;
            break;
         case 'f':
            atype = java.lang.Float.TYPE;
            break;
         case 'd':
            atype = java.lang.Double.TYPE;
            break;
         case 'o':
            atype = typeof(System.Object);
            break;
         case '*':
            atype = null; 
            break;
      }

      return atype;
   }

   /** marshalls the Sleep value into a Java value of the specified type. */
   public static Object buildArgument(Type type, Scalar value, ScriptInstance script)
   {
      if (type == typeof(java.lang.StringJ) || type == typeof (System.String))
      {
         return SleepUtils.isEmptyScalar(value) ? null : value.toString();
      }
      else if (value.getArray() != null)
      {
         if (type.isArray())
         {
            Type atype = getArrayType(value, type.getComponentType());

            Object arrayV = Array.newInstance(atype, value.getArray().size());
            java.util.Iterator<Object> i = value.getArray().scalarIterator();
            int x = 0;
            while (i.hasNext())
            {
                Scalar temp = (Scalar)i.next();
                Object blah = buildArgument(atype, temp, script);

                if ((blah == null && !atype.isPrimitive()) || atype.isInstance(blah) || atype.isPrimitive())
                {
                   Array.set(arrayV, x, blah);
                }
                else
                {
                   if (atype.isArray())
                   {
                      throw new java.lang.RuntimeException("incorrect dimensions for conversion to " + type);
                   }
                   else
                   {
                      throw new java.lang.RuntimeException(SleepUtils.describe(temp) + " at "+x+" is not compatible with " + atype.getName());
                   }
                }
                x++;
            }

            return arrayV;
         }
         else if (type == typeof(ScalarArray))
         {
            return value.objectValue();
         }
         else
         {
            return SleepUtils.getListFromArray(value);
         }
      }
      else if (value.getHash() != null)
      {
         if (type == typeof(ScalarHash))
         {
            return value.objectValue();
         }
         else
         {
            return SleepUtils.getMapFromHash(value);
         }
      }
      else if (type.isPrimitive())
      {
         if (type == java.lang.Boolean.TYPE)
         {
            return java.lang.Boolean.valueOf(value.intValue() != 0);
         }
         else if (type == java.lang.Byte.TYPE)
         {
            return new java.lang.Byte((byte)value.intValue());
         }
         else if (type == java.lang.Character.TYPE)
         {
            return new java.lang.Character(value.toString().charAt(0));
         }
         else if (type == java.lang.Double.TYPE)
         {
            return new java.lang.Double(value.doubleValue());
         }
         else if (type == java.lang.Float.TYPE)
         {
            return new Float((float)value.doubleValue());
         }
         else if (type == java.lang.Integer.TYPE)
         {
            return new java.lang.Integer(value.intValue());
         }
         else if (type == java.lang.Short.TYPE)
         {
            return new java.lang.Short((short)value.intValue());
         }
         else if (type == java.lang.Long.TYPE)
         {
            return new java.lang.Long(value.longValue());
         }
      }
      else if (SleepUtils.isEmptyScalar(value))
      {
         return null;
      }
      else if (type.isArray() && value.getActualValue().getType() == typeof(sleep.engine.types.StringValue))
      {
         if (type.getComponentType() == java.lang.Byte.TYPE || type.getComponentType() == typeof(Byte))
         {
            return BridgeUtilities.toByteArrayNoConversion(value.toString());
         }
         else if (type.getComponentType() == java.lang.Character.TYPE || type.getComponentType() == typeof(Character))
         {
            return value.toString().toCharArray();
         }
      }
      else if (type.isInterface() && SleepUtils.isFunctionScalar(value))
      {
         return ProxyInterface.BuildInterface(type, SleepUtils.getFunctionFromScalar(value, script), script);
      }

      return value.objectValue();
   }

   /** utility to create a string representation of an incompatible argument choice */
   public static String buildArgumentErrorMessage(Type theClass, String method, Type[] expected, Object[] parameters)
   {
      java.lang.StringBuffer tempa = new java.lang.StringBuffer(method + "(");
      
      for (int x = 0; x < expected.Length; x++)
      {
         tempa.append(expected[x].getName());

         if ((x + 1) < expected.Length)
            tempa.append(", ");
      }
      tempa.append(")");

      java.lang.StringBuffer tempb = new java.lang.StringBuffer("(");
      for (int x = 0; x < parameters.Length; x++)
      {
         if (parameters[x] != null)
            tempb.append(parameters[x].getClass().getName());
         else
            tempb.append("null");

         if ((x + 1) < parameters.Length)
            tempb.append(", ");
      }
      tempb.append(")");

      return "bad arguments " + tempb.toString() + " for " + tempa.toString() + " in " + theClass;
   } 

   /** populates a Java array with Sleep values marshalled into values of the specified types. */
   public static Object[] buildArgumentArray(Type[] types, java.util.Stack<Object> arguments, ScriptInstance script)
   {
      Object[] parameters = new Object[types.Length];

      for (int x = 0; x < parameters.Length; x++)
      {
         Scalar temp = (Scalar)arguments.pop();
         parameters[x] = buildArgument(types[x], temp, script);
      }
 
      return parameters;
   }

   /** marshalls a Java type into the appropriate Sleep scalar.  The primitives value will force this method to also check
       if the Java type could map to an int, long, double, etc.  Use true when in doubt. */
   public static Scalar BuildScalar(bool primitives, Object value)
   {
      if (value == null)
         return SleepUtils.getEmptyScalar();

      Type check = value.GetType();

      if (check.isArray())
      {
         if (check.getComponentType() == java.lang.Byte.TYPE || check.getComponentType() == typeof(java.lang.Byte))
         {
            return SleepUtils.getScalar((byte[])value);            
         }
         else if (check.getComponentType() == java.lang.Character.TYPE || check.getComponentType() == typeof(java.lang.Character))
         {
            return SleepUtils.getScalar(new String((char[])value));            
         }
         else
         {
            Scalar array = SleepUtils.getArrayScalar();
            for (int x = 0; x < java.util.Array.getLength(value); x++)
            {
               array.getArray().push(BuildScalar(true, java.util.Array.get(value, x)));
            }

            return array;
         }
      }

      if (primitives)
      {
         if (check.isPrimitive()) 
         { 
            check = normalizePrimitive(check); /* just in case, shouldn't be needed typically */
         }

         if (check == typeof(java.lang.Boolean))
         {
            return SleepUtils.getScalar(  ((java.lang.Boolean)value).booleanValue() ? 1 : 0 );
         }
         else if (check == typeof(java.lang.Byte))
         {
            return SleepUtils.getScalar(  (int)( ((java.lang.Byte)value).byteValue() )  );
         }
         else if (check == typeof(java.lang.Character))
         {
            return SleepUtils.getScalar(  value.toString()  );
         }
         else if (check == typeof(java.lang.Double))
         {
            return SleepUtils.getScalar(  ((java.lang.Double)value).doubleValue()   );
         }
         else if (check == typeof(java.lang.Float))
         {
            return SleepUtils.getScalar(  (double)( ((java.lang.Float)value).floatValue() )  );
         }
         else if (check == typeof(java.lang.Integer))
         {
            return SleepUtils.getScalar(  ((java.lang.Integer)value).intValue()   );
         }
         else if (check == typeof(LongValue))
         {
            return SleepUtils.getScalar(  ((java.lang.Long)value).longValue()   );
         }
      }

      if (check == typeof(java.lang.StringJ) || check == typeof(System.String))
      {
         return SleepUtils.getScalar(value.toString());
      }
      else if (check == typeof(Scalar) || check == typeof(WatchScalar)) 
      {
         return (Scalar)value;
      }
      else 
      {
         return SleepUtils.getScalar(value);
      }
   }

   /** Determines the primitive type of the specified array.  Primitive Sleep values (int, long, double) will return the appropriate Number.TYPE class.  This is an important distinction as Double.TYPE != new Double().getClass() */
   public static Type getArrayType(Scalar value, Type defaultc)
   {
      if (value.getArray() != null && value.getArray().size() > 0 && (defaultc == null || (
         defaultc == typeof (System.Object))))
      {
          for (int x = 0; x < value.getArray().size(); x++)
          {
             if (value.getArray().getAt(x).getArray() != null)
             {
                return getArrayType(value.getArray().getAt(x), defaultc);
             }

             Type  elem  = value.getArray().getAt(x).getValue().GetType();
             Object tempo = value.getArray().getAt(x).objectValue();

             if (elem == DOUBLE_SCALAR)
             {
                return java.lang.Double.TYPE;
             }
             else if (elem == INT_SCALAR)
             {
                return java.lang.Integer.TYPE;
             }
             else if (elem == LONG_SCALAR)
             {
                return java.lang.Long.TYPE;
             }
             else if (tempo != null)
             {
                return tempo.GetType();
             }
          }
      }

      return defaultc;
   }

   /** Standard method to handle a Java exception from a HOES call.  Basically this places the exception into Sleep's 
       throw mechanism and collects the stack frame. */
   public static void handleExceptionFromJava(java.lang.Throwable ex, ScriptEnvironment env, String description, int lineNumber)
   {
      if (ex != null)
      {                  
         env.flagError(ex);
 
         if (env.isThrownValue() && description != null && description.length() > 0)
         {
            env.getScriptInstance().recordStackFrame(description, lineNumber);
         }
      }
   }
}
}