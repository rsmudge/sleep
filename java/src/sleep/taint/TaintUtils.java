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
package sleep.taint;

import sleep.engine.*;
import sleep.interfaces.*;
import sleep.runtime.*;

import sleep.bridges.*;
import sleep.engine.types.*;

import java.util.*;

/** <p>Sleep supports a mode where variables received from external sources are considered tainted.  This is a security mechanism to help educate
    scripters when they may be using tainted data within dangerous operations.</p>

    <p>Terminology used here comes from <a href="http://news.php.net/php.internals/26979">Run-time taint support proposal</a> by Wietse Venema posted to the 
    PHP internals mailing list.</p>

    <p>Sleep's implementation of taint is designed to have little to no runtime impact when turned off.  When enabled taint mode wraps operations within the
    Sleep interpreter with taint wrappers.  These wrappers check if any of the arguments on the current "frame" are tainted.  If the answer is yes then the
    original operation is executed as normal and the return value is tainted.</p>
   
    <p>Wrapped operations include operations and function calls.  Parsed literals are treated as a special case.</p>

    <p>Sleep has 4 categories of functions and their relation to tainted values:</p>
   
    <ul>
     <li><b>Sensitive</b> - functions that are not allowed to receive a tainted input.  Any attempt to send tainted input will immediately throw a runtime exception.  Sleep
     functions in this category are responsible for making themselves known.  The mechanism for this is described below.</li>
     <li><b>Permeable</b> - functions or primitives that return a tainted result only when their input is tainted.  By default all Sleep functions fall into this category.</li>
     <li><b>Tainters</b> - functions that always return tainted results.  These functions are expected to self identify as well.</li>
     <li><b>Sanitizers</b> - functions that always return untainted results.</li>
   </ul>

   <p>The taint mechanism depends on bridge writers and application developers to flag their Sleep extensions into the appropriate category.  With this in mind Sleep tries to make
   this process as easy and transparent as possible.</p>

   <pre>
   public void scriptLoaded(ScriptInstance si)
   {
      // install &amp;foo as a Tainter function.
      si.getScriptEnvironment().getEnvironment().put("&amp;foo", TaintUtils.Tainter(this));

      // install &amp;bar as a Sanitizer function.
      si.getScriptEnvironment().getEnvironment().put("&amp;bar", TaintUtils.Sanitizer(this));

      // install &amp;dbquery as a Sensitive function.
      si.getScriptEnvironment().getEnvironment().put("&amp;dbquery", TaintUtils.Sensitive(this));
   }</pre>   

   <p>The TaintUtils class contains static methods that accept different Sleep bridges as parameters.  They return wrapped versions of these bridges if tainting is enabled.    
   If tainting is disabled these functions merely return the original bridges that were passed in.  If you're writing a bridge you merely need to identify which of your functions
   are permeable or tainters and wrap them using one static call listed here.</p>
*/
public class TaintUtils
{
    /** one global static variable to determine if Sleep is running in taint mode or not */
    private static boolean isTaintMode = System.getProperty("sleep.taint", "false").equals("true");

    /** checks if Sleep is in taint mode or not.  This value does not change during runtime */
    public static boolean isTaintMode()
    {
//       return true;
       return isTaintMode;
    }

    /** taints the specified scalar (if it is a value scalar only).  returns the original container.  If tainting is disabled the original bridge is returned. */
    public static Scalar taint(Scalar value)
    {
       if (isTaintMode() && value.getActualValue() != null)
       {
          value.setValue(new TaintedValue(value.getActualValue()));
       }

       return value;
    }

    /** taints all of the Scalar values in the specified stack.  More fun that a barrel full of monkeys.  this function acts on the passed in stack */
    public static Stack taint(Stack values)
    {
       if (isTaintMode())
       {
          Iterator i = values.iterator();
          while (i.hasNext())
          {
             taintAll((Scalar)i.next());
          }
       }
       return values;
    }

    /** taints the specified scalar (bridge writers should call this on their scalars).  recurses on hashes and arrays.  returns the original container.  If tainting is disabled the original bridge is returned. not safe for circular data structures. */
    public static Scalar taintAll(Scalar value)
    {
       if (value.getArray() != null && value.getArray().getClass() == CollectionWrapper.class)
       {
          value.setValue(new TaintArray(value.getArray()));
       }
       else if (value.getArray() != null)
       {
          Iterator i = value.getArray().scalarIterator();
          while (i.hasNext())
          {
             taintAll((Scalar)i.next());
          }
       }
       else if (value.getHash() != null && value.getHash().getClass() == MapWrapper.class)
       {
          value.setValue(new TaintHash(value.getHash()));
       }
       else if (value.getHash() != null)
       {
          Iterator i = value.getHash().getData().entrySet().iterator();
          while (i.hasNext())
          {
             Map.Entry tempe = (Map.Entry)i.next();
             taintAll((Scalar)tempe.getValue());
          }
       }
       else if (value.getActualValue().getType() == ObjectValue.class && value.objectValue().getClass() == KeyValuePair.class)
       {
          KeyValuePair kvp = (KeyValuePair)value.objectValue();
          value.setValue(SleepUtils.getScalar(new KeyValuePair(kvp.getKey(), TaintUtils.taintAll(kvp.getValue()))));
       }
       else if (value.getActualValue() != null)
       {
          value.setValue(new TaintedValue(value.getActualValue()));
       }

       return value;
    }

    /** untaints the specified scalar.  returns the original container. */
    public static Scalar untaint(Scalar value)
    {
       if (value.getActualValue() != null && value.getActualValue().getClass() == TaintedValue.class)
       {
          value.setValue(((TaintedValue)value.getActualValue()).untaint());
       }
       return value;
    }

    /** check if a value is tainted honoring circular dependencies */
    private static boolean isTainted(Set seen, Scalar value)
    {
       if (value.getHash() != null)
       {
          if (!seen.contains(value.getHash()))
          {
             seen.add(value.getHash());

             Iterator i = value.getHash().getData().values().iterator();
             while (i.hasNext())
             {
                if (isTainted(seen, (Scalar)i.next()))
                {
                   return true;
                }
             }
          }

          return false;
       }
       else if (value.getArray() != null)
       {
          if (!seen.contains(value.getArray()))
          {
             seen.add(value.getArray());

             Iterator i = value.getArray().scalarIterator();
             while (i.hasNext())
             {
                if (isTainted(seen, (Scalar)i.next()))
                {
                   return true;
                }
             }
          }

          return false;
       }
       else
       {
          return value.getActualValue().getClass() == TaintedValue.class && !SleepUtils.isEmptyScalar(value);
       }
    }

    /** checks if a scalar is tainted */
    public static boolean isTainted(Scalar value)
    {
       if (value.getActualValue() == null)
       {
          return isTainted(new HashSet(), value);
       }
       else
       {
          return isTainted(null, value);
       }
    }

    /** Wraps the specified bridge in such a way that all results are considered sanitized (untainted).  If tainting is disabled the original bridge is returned. */
    public static Object Sanitizer(Object f)
    {
       if (isTaintMode())
       {
          return new Sanitizer(f);
       }
       else
       {
          return f;
       }
    }

    /** Wraps the specified bridge in such a way that all results are considered tainted.  If tainting is disabled the original bridge is returned. */
    public static Object Tainter(Object f)
    {
       if (isTaintMode())
       {
          return new Tainter(f);
       }
       else
       {
          return f;
       }
    }
 
    /** Wraps the specified bridge in such a way that all values on current frame are checked for tainted values.  Any tainted values will result in an exception
        preventing the function from being called.  If tainting is disabled then the original bridge is returned. */
    public static Object Sensitive(Object f)
    {
       if (isTaintMode())
       {
          return new Sensitive(f);
       }
       else
       {
          return f;
       }
    }

    /** checks the specified argument stack for tainted values.  If there are tainted values a comma separated string description is returned.  Otherwise null is returned. */
    public static String checkArguments(Stack arguments)
    {
       Stack values = new Stack(); /* track all of our tainted values */
       String desc  = null;

       Iterator i = arguments.iterator();
       while (i.hasNext())
       {
          Scalar argument = (Scalar)i.next();
          if (TaintUtils.isTainted(argument))
          {
             values.push(argument);
          }
       }

       if (!values.isEmpty())
       {
          desc = SleepUtils.describe(values);
       }        

       return desc;
    }
}
