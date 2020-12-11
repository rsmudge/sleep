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
using  sleep.runtime;

namespace sleep.bridges{
 
/** provides some of the basic number crunching functionality */
public class BasicNumbers : sleep.interfaces.Predicate, Operator, Loadable, Function
{
    public void scriptUnloaded(ScriptInstance aScript)
    {
    }

    public void scriptLoaded(ScriptInstance aScript)
    {
       java.util.Hashtable<Object,Object> temp = aScript.getScriptEnvironment().getEnvironment();

       Object sanitized = sleep.taint.TaintUtils.Sanitizer(this);

       // math ops..

       String[] funcs = new String[] { "&abs", "&acos", "&asin", "&atan", "&atan2", "&ceil", "&cos", "&log", "&round", 
                                       "&sin", "&sqrt", "&tan", "&radians", "&degrees", "&exp", "&floor", "&sum" };

       for (int x = 0; x < funcs.Length; x++)
       {
          temp.put(funcs[x], sanitized);
       }

       // functions
       temp.put("&double", sanitized);
       temp.put("&int", sanitized);
       temp.put("&uint", sanitized);
       temp.put("&long", sanitized);

       temp.put("&parseNumber",  sanitized);
       temp.put("&formatNumber", sanitized);

       // basic operators
       temp.put("+", sanitized);
       temp.put("-", sanitized);
       temp.put("/", sanitized);
       temp.put("*", sanitized);
       temp.put("**", sanitized); // exponentation

       /* why "% "?  we had an amibiguity with %() to initialize hash literals and n % (expr) 
          for normal math ops.  the initial parser in the case of mod will preserve one bit of
          whitespace to try to prevent mass hysteria and confusion to the parser for determining
          wether an op is being used or a hash literal is being initialized */
       temp.put("% ", sanitized);

       temp.put("<<", sanitized);
       temp.put(">>", sanitized);
       temp.put("&", sanitized);
       temp.put("|", sanitized);
       temp.put("^", sanitized);
       temp.put("&not", sanitized);
 
       // predicates
       temp.put("==", this);
       temp.put("!=", this);
       temp.put("<=", this);
       temp.put(">=", this);
       temp.put("<",  this);
       temp.put(">",  this);
       temp.put("is", this);

       // functions
       temp.put("&rand", sanitized);
       temp.put("&srand", sanitized);
    }

    public Scalar evaluate(String name, ScriptInstance si, java.util.Stack<Object> args)
    {
       if (name.equals("&abs")) { return SleepUtils.getScalar(java.lang.Math.abs(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&acos")) { return SleepUtils.getScalar(java.lang.Math.acos(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&asin")) { return SleepUtils.getScalar(java.lang.Math.asin(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&atan")) { return SleepUtils.getScalar(java.lang.Math.atan(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&atan2")) { return SleepUtils.getScalar(java.lang.Math.atan2(BridgeUtilities.getDouble(args, 0.0), BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&ceil")) { return SleepUtils.getScalar(java.lang.Math.ceil(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&floor")) { return SleepUtils.getScalar(java.lang.Math.floor(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&cos")) { return SleepUtils.getScalar(java.lang.Math.cos(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&log") && args.size() == 1) { return SleepUtils.getScalar(java.lang.Math.log(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&log") && args.size() == 2) { return SleepUtils.getScalar(java.lang.Math.log(BridgeUtilities.getDouble(args, 0.0)) / Math.log(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&round")) { 
           if (args.size() == 1)
           {
              return SleepUtils.getScalar(java.lang.Math.round(BridgeUtilities.getDouble(args, 0.0))); 
           }
           else
           {
              /* round to a certain number of places--if the argument is significantly large, this function could break */
              double number = BridgeUtilities.getDouble(args, 0.0);
              double places = Math.pow(10, BridgeUtilities.getInt(args, 0));

              number = Math.round(number * places);
              number = number / places;
              return SleepUtils.getScalar(number);
           }
       }
       else if (name.equals("&sin")) { return SleepUtils.getScalar(java.lang.Math.sin(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&sqrt")) { return SleepUtils.getScalar(java.lang.Math.sqrt(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&tan")) { return SleepUtils.getScalar(java.lang.Math.tan(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&radians")) { return SleepUtils.getScalar(java.lang.Math.toRadians(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&degrees")) { return SleepUtils.getScalar(java.lang.Math.toDegrees(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&exp")) { return SleepUtils.getScalar(java.lang.Math.exp(BridgeUtilities.getDouble(args, 0.0))); }
       else if (name.equals("&sum"))
       {
           java.util.Iterator<Object> i = BridgeUtilities.getIterator(args, si);

           java.util.List<Object> iterators = null;
           if (args.size() >= 1)
           {
              /* auxillary iterators */
              iterators = new java.util.LinkedList<Object>();
              while (!args.isEmpty())
              {
                 iterators.add(BridgeUtilities.getIterator(args, si));
              }
           }

           double result = 0.0;
           double temp;

           /* this is a simple sum of an array or iterator */
           if (iterators == null)
           {
              while (i.hasNext())
              {
                 result += ((Scalar)i.next()).doubleValue();
              }
           }
           /* this is for summing the products of multiple arrays or iterators */
           else
           {
              while (i.hasNext())
              {
                 temp = ((Scalar)i.next()).doubleValue();

                 java.util.Iterator<Object> j = iterators.iterator();
                 while (j.hasNext())
                 {
                    java.util.Iterator<Object> tempi = (java.util.Iterator<Object>)j.next();
                    if (tempi.hasNext())
                    {
                       temp *= ((Scalar)tempi.next()).doubleValue();
                    }
                    else
                    {
                       temp = 0.0;
                       break;
                    }
                 }

                 result += temp;
              }
           }

           return SleepUtils.getScalar(result);
       }
       else if (name.equals("&not")) 
       {
           ScalarType sa = ((Scalar)args.pop()).getActualValue(); /* we already assume this is a number */

           if (sa.getType() == typeof(IntValue))
               return SleepUtils.getScalar(~ sa.intValue());

           return SleepUtils.getScalar(~ sa.longValue());
       }
       else if (name.equals("&long"))
       {
          Scalar temp = BridgeUtilities.getScalar(args);
          return SleepUtils.getScalar(temp.longValue());
       }
       else if (name.equals("&double"))
       {
          Scalar temp = BridgeUtilities.getScalar(args);
          return SleepUtils.getScalar(temp.doubleValue());
       }
       else if (name.equals("&int"))
       {
          Scalar temp = BridgeUtilities.getScalar(args);
          return SleepUtils.getScalar(temp.intValue());
       }
       else if (name.equals("&uint"))
       {
          int temp = BridgeUtilities.getInt(args, 0);
          long templ = 0x00000000FFFFFFFFL & temp;
          return SleepUtils.getScalar(templ);
       }
       else if (name.equals("&parseNumber"))
       {
          String number = BridgeUtilities.getString(args, "0");
          int    radix  = BridgeUtilities.getInt(args, 10);

          java.math.BigInteger temp = new java.math.BigInteger(number, radix);
          return SleepUtils.getScalar(temp.longValue());
       }
       else if (name.equals("&formatNumber"))
       {
          String number = BridgeUtilities.getString(args, "0");

          int from = 10, to = 10;

          if (args.size() == 2)
          {
             from = BridgeUtilities.getInt(args, 10);
          }

          to = BridgeUtilities.getInt(args, 10);

          java.math.BigInteger temp = new java.math.BigInteger(number, from);
          return SleepUtils.getScalar(temp.toString(to));
       }
       else if (name.equals("&srand"))
       {
          long seed = BridgeUtilities.getLong(args);
          si.getMetadata().put("__RANDOM__", new java.util.Random(seed));
       }
       else if (name.equals("&rand"))
       {
          if (si.getMetadata().get("__RANDOM__") == null) 
          { 
             si.getMetadata().put("__RANDOM__", new java.util.Random()); 
          }
          java.util.Random r = (java.util.Random)si.getMetadata().get("__RANDOM__");

          if (! args.isEmpty())
          {
             Scalar temp = (Scalar)args.pop();

             if (temp.getArray() != null)
             {
                int potential = r.nextInt(temp.getArray().size());
                return temp.getArray().getAt(potential);
             }
             else
             {
                return SleepUtils.getScalar(r.nextInt(temp.intValue()));
             }
          }
          
          return SleepUtils.getScalar(r.nextDouble());
       }

       return SleepUtils.getEmptyScalar();
    }

    public bool decide(String n, ScriptInstance i, java.util.Stack<Object> l)
    {
       java.util.Stack<Object> env = i.getScriptEnvironment().getEnvironmentStack();
       Scalar vb = (Scalar)l.pop();
       Scalar va = (Scalar)l.pop();

       if (n.equals("is"))
          return va.objectValue() == vb.objectValue(); /* could be anything! */

       ScalarType sb = vb.getActualValue();
       ScalarType sa = va.getActualValue();

       if (sa.getType() == typeof(DoubleValue) || sb.getType() == typeof(DoubleValue))
       {
          double a = sa.doubleValue();
          double b = sb.doubleValue();

          if (n.equals("==")) { return a == b; }
          if (n.equals("!=")) { return a != b; }
          if (n.equals("<=")) { return a <= b; }
          if (n.equals(">=")) { return a >= b; }
          if (n.equals("<"))  { return a <  b; }
          if (n.equals(">"))  { return a >  b; }
       }
       else if (sa.getType() == typeof(LongValue) || sb.getType() == typeof(LongValue))
       {
          long a = sa.longValue();
          long b = sb.longValue();

          if (n.equals("==")) { return a == b; }
          if (n.equals("!=")) { return a != b; }
          if (n.equals("<=")) { return a <= b; }
          if (n.equals(">=")) { return a >= b; }
          if (n.equals("<"))  { return a <  b; }
          if (n.equals(">"))  { return a >  b; }
       }
       else
       {
          int a = sa.intValue();
          int b = sb.intValue();

          if (n.equals("==")) { return a == b; }
          if (n.equals("!=")) { return a != b; }
          if (n.equals("<=")) { return a <= b; }
          if (n.equals(">=")) { return a >= b; }
          if (n.equals("<"))  { return a <  b; }
          if (n.equals(">"))  { return a >  b; }
       }

       return false;
    }

    public Scalar operate(String o, ScriptInstance i, java.util.Stack<Object> locals)
    {
       ScalarType left  = ((Scalar)locals.pop()).getActualValue();
       ScalarType right = ((Scalar)locals.pop()).getActualValue();

       if ((right.getType() == typeof(DoubleValue) || left.getType() == typeof(DoubleValue)) && !(o.equals(">>") || o.equals("<<") || o.equals("&") || o.equals("|") || o.equals("^")))
       {
          double a = left.doubleValue();
          double b = right.doubleValue();

          if (o.equals("+")) { return SleepUtils.getScalar(a + b); }
          if (o.equals("-")) { return SleepUtils.getScalar(a - b); }
          if (o.equals("*")) { return SleepUtils.getScalar(a * b); }
          if (o.equals("/")) { return SleepUtils.getScalar(a / b); }
          if (o.equals("% ")) { return SleepUtils.getScalar(a % b); }
          if (o.equals("**")) { return SleepUtils.getScalar(java.lang.Math.pow((double)a, (double)b)); }
       }
       else if (right.getType() == typeof(LongValue) || left.getType() == typeof(LongValue))
       {
          long a = left.longValue();
          long b = right.longValue();

          if (o.equals("+")) { return SleepUtils.getScalar(a + b); }
          if (o.equals("-")) { return SleepUtils.getScalar(a - b); }
          if (o.equals("*")) { return SleepUtils.getScalar(a * b); }
          if (o.equals("/")) { return SleepUtils.getScalar(a / b); }
          if (o.equals("% ")) { return SleepUtils.getScalar(a % b); }
          if (o.equals("**")) { return SleepUtils.getScalar(java.lang.Math.pow((double)a, (double)b)); }
          if (o.equals(">>"))  { return SleepUtils.getScalar(a >> b); }
          if (o.equals("<<"))  { return SleepUtils.getScalar(a << b); }
          if (o.equals("&"))  { return SleepUtils.getScalar(a & b); }
          if (o.equals("|"))  { return SleepUtils.getScalar(a | b); }
          if (o.equals("^"))  { return SleepUtils.getScalar(a ^ b); }
       }
       else
       {
          int a = left.intValue();
          int b = right.intValue();

          if (o.equals("+")) { return SleepUtils.getScalar(a + b); }
          if (o.equals("-")) { return SleepUtils.getScalar(a - b); }
          if (o.equals("*")) { return SleepUtils.getScalar(a * b); }
          if (o.equals("/")) { return SleepUtils.getScalar(a / b); }
          if (o.equals("% ")) { return SleepUtils.getScalar(a % b); }
          if (o.equals("**")) { return SleepUtils.getScalar(java.lang.Math.pow((double)a, (double)b)); }
          if (o.equals(">>"))  { return SleepUtils.getScalar(a >> b); }
          if (o.equals("<<"))  { return SleepUtils.getScalar(a << b); }
          if (o.equals("&"))  { return SleepUtils.getScalar(a & b); }
          if (o.equals("|"))  { return SleepUtils.getScalar(a | b); }
          if (o.equals("^"))  { return SleepUtils.getScalar(a ^ b); }
       }

       return SleepUtils.getEmptyScalar();
    }
}
}