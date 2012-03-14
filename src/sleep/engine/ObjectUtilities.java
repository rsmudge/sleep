/*
   SLEEP - Simple Language for Environment Extension Purposes
 .------------------------------.
 | sleep.engine.ObjectUtilities |_____________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.hick.org/~raffi/

   Description: A bunch of utility methods for Sleeps object interface: HOES
           Haphazard Object Extensions for Sleep

   Documentation:

   Changelog:
   03/19/2005 - rewrote the argument matching stuff...
   03/12/2005 - date created

   * This software is distributed under the artistic license, see license.txt
     for more information. *

 |____________________________________________________________________________|
 */

package sleep.engine;

import java.lang.reflect.*;
import java.util.*;

import sleep.runtime.*;

import sleep.engine.types.*;
import sleep.interfaces.Function;

import sleep.bridges.*;

/** This class is sort of the center of the HOES universe containing several methods for mapping 
    between Sleep and Java and resolving which mappings make sense. */
public class ObjectUtilities
{
   private static Class STRING_SCALAR;
   private static Class INT_SCALAR; 
   private static Class DOUBLE_SCALAR;
   private static Class LONG_SCALAR;
   private static Class OBJECT_SCALAR;

   static
   {
      STRING_SCALAR = sleep.engine.types.StringValue.class;
      INT_SCALAR    = sleep.engine.types.IntValue.class;
      DOUBLE_SCALAR = sleep.engine.types.DoubleValue.class;
      LONG_SCALAR   = sleep.engine.types.LongValue.class;
      OBJECT_SCALAR = sleep.engine.types.ObjectValue.class;
   }

   /** when looking for a Java method that matches the sleep args, we use a Yes match immediately */
   public static final int ARG_MATCH_YES   = 3;
  
   /** when looking for a Java method that matches the sleep args, we immediately drop all of the no answers. */
   public static final int ARG_MATCH_NO    = 0;

   /** when looking for a Java method that matches the sleep args, we save the maybes and use them as a last resort if no yes match is found */
   public static final int ARG_MATCH_MAYBE = 1;

   /** convienence method to determine wether or not the stack of values is a safe match for the specified method signature */
   public static int isArgMatch(Class[] check, Stack arguments)
   {
      int value = ARG_MATCH_YES;

      for (int z = 0; z < check.length; z++)
      {
         Scalar scalar = (Scalar)arguments.get(check.length - z - 1);

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
   private static Class normalizePrimitive(Class check)
   {
      if (check == Integer.TYPE) { check = Integer.class; }
      else if (check == Double.TYPE)   { check = Double.class; }
      else if (check == Long.TYPE)     { check = Long.class; }
      else if (check == Float.TYPE)    { check = Float.class; }
      else if (check == Boolean.TYPE)  { check = Boolean.class; }
      else if (check == Byte.TYPE)     { check = Byte.class; }
      else if (check == Character.TYPE) { check = Character.class; }
      else if (check == Short.TYPE)    { check = Short.class; }

      return check;
   }

   /** determined if the specified scalar can be rightfully cast to the specified class */
   public static int isArgMatch(Class check, Scalar scalar)
   {
      if (SleepUtils.isEmptyScalar(scalar))
      {
         return ARG_MATCH_YES;
      }
      else if (scalar.getArray() != null)
      {
         if (check.isArray())
         {
            Class compType = check.getComponentType(); /* find the actual nuts and bolts component type so we can work with it */
            while (compType.isArray())
            {
               compType = compType.getComponentType();
            }

            Class mytype = getArrayType(scalar, null);
 
            if (mytype != null && compType.isAssignableFrom(mytype))
            {
               return ARG_MATCH_YES;
            }
            else
            {
               return ARG_MATCH_NO;
            }
         }
         else if (check == java.util.List.class || check == java.util.Collection.class)
         {
            // would a java.util.List or java.util.Collection satisfy the argument?
            return ARG_MATCH_YES;
         }
         else if (check == ScalarArray.class)
         {
            return ARG_MATCH_YES;
         }
         else if (check == java.lang.Object.class)
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
         if (check == java.util.Map.class)
         {
            // would a java.util.Map or java.util.Collection satisfy the argument?
            return ARG_MATCH_YES;
         }
         else if (check == ScalarHash.class)
         {
            return ARG_MATCH_YES;
         }
         else if (check == java.lang.Object.class)
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
         Class stemp = scalar.getActualValue().getType(); /* at this point we know scalar is not null, not a hash, and not an array */

         if (stemp == INT_SCALAR && check == Integer.TYPE)
         {
            return ARG_MATCH_YES;
         }
         else if (stemp == DOUBLE_SCALAR && check == Double.TYPE)
         {
            return ARG_MATCH_YES;
         }
         else if (stemp == LONG_SCALAR && check == Long.TYPE)
         {
            return ARG_MATCH_YES;
         }
         else if (check == Character.TYPE && stemp == STRING_SCALAR && scalar.getActualValue().toString().length() == 1)
         {
            return ARG_MATCH_YES;
         }
         else if (stemp == OBJECT_SCALAR)
         {
            check = normalizePrimitive(check);
            return (scalar.objectValue().getClass() == check) ? ARG_MATCH_YES : ARG_MATCH_NO;
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
      else if (check == String.class)
      {
         Class stemp = scalar.getActualValue().getType();
         return (stemp == STRING_SCALAR) ? ARG_MATCH_YES : ARG_MATCH_MAYBE;
      }
      else if (check == Object.class)
      {
         return ARG_MATCH_MAYBE; /* we're vying for anything and this will match anything */
      }
      else if (check.isInstance(scalar.objectValue()))
      {
         Class stemp = scalar.getActualValue().getType();
         return (stemp == OBJECT_SCALAR) ? ARG_MATCH_YES : ARG_MATCH_MAYBE;
      }
      else if (check.isArray())
      {
         Class stemp = scalar.getActualValue().getType();
         if (stemp == STRING_SCALAR && (check.getComponentType() == Character.TYPE || check.getComponentType() == Byte.TYPE))
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
   public static Method findMethod(Class theClass, String method, Stack arguments)
   {
      int      size    = arguments.size();

      Method   temp    = null;
      Method[] methods = theClass.getMethods();

      for (int x = 0; x < methods.length; x++) 
      {
         if (methods[x].getName().equals(method) && methods[x].getParameterTypes().length == size)
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
   public static Constructor findConstructor(Class theClass, Stack arguments)
   {
      int      size    = arguments.size();

      Constructor   temp         = null;
      Constructor[] constructors = theClass.getConstructors();

      for (int x = 0; x < constructors.length; x++) 
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
   public static Class convertScalarDescriptionToClass(Scalar description)
   {
       if (description.objectValue() instanceof Class)
       {
          return (Class)description.objectValue();
       }

       return convertDescriptionToClass(description.toString());
   }

   /** converts the one character class description to the specified Class type, i.e. z = boolean, c = char, b = byte, i = integer, etc.. */
   public static Class convertDescriptionToClass(String description)
   {
      if (description.length() != 1)
      {
         return null;
      }

      Class atype = null;

      switch (description.charAt(0))
      {
         case 'z':
            atype = Boolean.TYPE;
            break;
         case 'c':
            atype = Character.TYPE;
            break;
         case 'b':
            atype = Byte.TYPE;
            break;
         case 'h':
            atype = Short.TYPE;
            break;
         case 'i':
            atype = Integer.TYPE;
            break;
         case 'l':
            atype = Long.TYPE;
            break;
         case 'f':
            atype = Float.TYPE;
            break;
         case 'd':
            atype = Double.TYPE;
            break;
         case 'o':
            atype = Object.class;
            break;
         case '*':
            atype = null; 
            break;
      }

      return atype;
   }

   /** marshalls the Sleep value into a Java value of the specified type. */
   public static Object buildArgument(Class type, Scalar value, ScriptInstance script)
   {
      if (type == String.class)
      {
         return SleepUtils.isEmptyScalar(value) ? null : value.toString();
      }
      else if (value.getArray() != null)
      {
         if (type.isArray())
         {
            Class atype = getArrayType(value, type.getComponentType());

            Object arrayV = Array.newInstance(atype, value.getArray().size());
            Iterator i = value.getArray().scalarIterator();
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
                      throw new RuntimeException("incorrect dimensions for conversion to " + type);
                   }
                   else
                   {
                      throw new RuntimeException(SleepUtils.describe(temp) + " at "+x+" is not compatible with " + atype.getName());
                   }
                }
                x++;
            }

            return arrayV;
         }
         else if (type == ScalarArray.class)
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
         if (type == ScalarHash.class)
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
         if (type == Boolean.TYPE)
         {
            return Boolean.valueOf(value.intValue() != 0);
         }
         else if (type == Byte.TYPE)
         {
            return new Byte((byte)value.intValue());
         }
         else if (type == Character.TYPE)
         {
            return new Character(value.toString().charAt(0));
         }
         else if (type == Double.TYPE)
         {
            return new Double(value.doubleValue());
         }
         else if (type == Float.TYPE)
         {
            return new Float((float)value.doubleValue());
         }
         else if (type == Integer.TYPE)
         {
            return new Integer(value.intValue());
         }
         else if (type == Short.TYPE)
         {
            return new Short((short)value.intValue());
         }
         else if (type == Long.TYPE)
         {
            return new Long(value.longValue());
         }
      }
      else if (SleepUtils.isEmptyScalar(value))
      {
         return null;
      }
      else if (type.isArray() && value.getActualValue().getType() == sleep.engine.types.StringValue.class)
      {
         if (type.getComponentType() == Byte.TYPE || type.getComponentType() == Byte.class)
         {
            return BridgeUtilities.toByteArrayNoConversion(value.toString());
         }
         else if (type.getComponentType() == Character.TYPE || type.getComponentType() == Character.class)
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
   public static String buildArgumentErrorMessage(Class theClass, String method, Class[] expected, Object[] parameters)
   {
      StringBuffer tempa = new StringBuffer(method + "(");
      
      for (int x = 0; x < expected.length; x++)
      {
         tempa.append(expected[x].getName());

         if ((x + 1) < expected.length)
            tempa.append(", ");
      }
      tempa.append(")");

      StringBuffer tempb = new StringBuffer("(");
      for (int x = 0; x < parameters.length; x++)
      {
         if (parameters[x] != null)
            tempb.append(parameters[x].getClass().getName());
         else
            tempb.append("null");

         if ((x + 1) < parameters.length)
            tempb.append(", ");
      }
      tempb.append(")");

      return "bad arguments " + tempb.toString() + " for " + tempa.toString() + " in " + theClass;
   } 

   /** populates a Java array with Sleep values marshalled into values of the specified types. */
   public static Object[] buildArgumentArray(Class[] types, Stack arguments, ScriptInstance script)
   {
      Object[] parameters = new Object[types.length];

      for (int x = 0; x < parameters.length; x++)
      {
         Scalar temp = (Scalar)arguments.pop();
         parameters[x] = buildArgument(types[x], temp, script);
      }
 
      return parameters;
   }

   /** marshalls a Java type into the appropriate Sleep scalar.  The primitives value will force this method to also check
       if the Java type could map to an int, long, double, etc.  Use true when in doubt. */
   public static Scalar BuildScalar(boolean primitives, Object value)
   {
      if (value == null)
         return SleepUtils.getEmptyScalar();

      Class check = value.getClass();

      if (check.isArray())
      {
         if (check.getComponentType() == Byte.TYPE || check.getComponentType() == Byte.class)
         {
            return SleepUtils.getScalar((byte[])value);            
         }
         else if (check.getComponentType() == Character.TYPE || check.getComponentType() == Character.class)
         {
            return SleepUtils.getScalar(new String((char[])value));            
         }
         else
         {
            Scalar array = SleepUtils.getArrayScalar();
            for (int x = 0; x < Array.getLength(value); x++)
            {
               array.getArray().push(BuildScalar(true, Array.get(value, x)));
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

         if (check == Boolean.class)
         {
            return SleepUtils.getScalar(  ((Boolean)value).booleanValue() ? 1 : 0 );
         }
         else if (check == Byte.class)
         {
            return SleepUtils.getScalar(  (int)( ((Byte)value).byteValue() )  );
         }
         else if (check == Character.class)
         {
            return SleepUtils.getScalar(  value.toString()  );
         }
         else if (check == Double.class)
         {
            return SleepUtils.getScalar(  ((Double)value).doubleValue()   );
         }
         else if (check == Float.class)
         {
            return SleepUtils.getScalar(  (double)( ((Float)value).floatValue() )  );
         }
         else if (check == Integer.class)
         {
            return SleepUtils.getScalar(  ((Integer)value).intValue()   );
         }
         else if (check == Long.class)
         {
            return SleepUtils.getScalar(  ((Long)value).longValue()   );
         }
      }

      if (check == String.class)
      {
         return SleepUtils.getScalar(value.toString());
      }
      else if (check == Scalar.class || check == WatchScalar.class) 
      {
         return (Scalar)value;
      }
      else 
      {
         return SleepUtils.getScalar(value);
      }
   }

   /** Determines the primitive type of the specified array.  Primitive Sleep values (int, long, double) will return the appropriate Number.TYPE class.  This is an important distinction as Double.TYPE != new Double().getClass() */
   public static Class getArrayType(Scalar value, Class defaultc)
   {
      if (value.getArray() != null && value.getArray().size() > 0 && (defaultc == null || defaultc == Object.class))
      {
          for (int x = 0; x < value.getArray().size(); x++)
          {
             if (value.getArray().getAt(x).getArray() != null)
             {
                return getArrayType(value.getArray().getAt(x), defaultc);
             }

             Class  elem  = value.getArray().getAt(x).getValue().getClass();
             Object tempo = value.getArray().getAt(x).objectValue();

             if (elem == DOUBLE_SCALAR)
             {
                return Double.TYPE;
             }
             else if (elem == INT_SCALAR)
             {
                return Integer.TYPE;
             }
             else if (elem == LONG_SCALAR)
             {
                return Long.TYPE;
             }
             else if (tempo != null)
             {
                return tempo.getClass();
             }
          }
      }

      return defaultc;
   }

   /** Standard method to handle a Java exception from a HOES call.  Basically this places the exception into Sleep's 
       throw mechanism and collects the stack frame. */
   public static void handleExceptionFromJava(Throwable ex, ScriptEnvironment env, String description, int lineNumber)
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
