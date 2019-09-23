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
import java.io.Serializable;
import sleep.runtime.*;

/** <p>A check object is the internal atomic step representing a predicate condition.  This API is exposed to allow developers
  * implementing a PredicateEnvironment bridge to take advantage of this API.</p>
  *
  * <p>Everything required to setup the predicate check, evaluate all the ands/ors, and return true or false is handled by this
  * atomic step.</p>
  *
  * <p>To better illustrate what a Check is:</p>
  *
  * <pre> if ($x == 3)
  * { 
  *    println("\$x is 3");
  * }</pre>
  *
  * <p>The above sleep code would be compiled into a sequence that looks like:</p>
  *
  * <pre>[Goto]:  (loop: false)
  * <b> [Condition]:
  *      [Predicate]: name-&gt;==  negated-&gt;false
  *         [Setup]:
  *            [Create Frame]
  *            [Scalar]: $x
  *            [Push]
  *            [Scalar]: 3
  *            [Push]</b>
  *  [If true]:
  *      [Create Frame]
  *      [Parsed Literal]  null
  *         [Element]: $x is 3
  *      [Push]
  *      [Function Call]: &println
  *  [If False]:</pre>
  *
  * <p>The parts that are bolded are representative of the word done by the Check object.</p>
  *
  * @see sleep.interfaces.PredicateEnvironment
  */
public class CheckEval implements Check, Serializable
{
   private Check   iftrue;
   private Check   iffalse;
   private Block   setup;
   private boolean negate;

   public String name; 

   /** Converts this object to a string, used by the sleep engine for constructing an AST like thing */
   public String toString(String prefix)
   {     
       StringBuffer temp = new StringBuffer();
       temp.append(prefix);
       temp.append("[Predicate]: ");
           temp.append("name->");
           temp.append(name);
           temp.append("  negated->");
           temp.append(negate);
           temp.append("\n");
       temp.append(prefix);
       temp.append("   ");
       temp.append("[Setup]: \n");
       temp.append(setup.toString(prefix+"      "));

       return temp.toString();
   }

   /** Returns a string representation of this object */
   public String toString()
   {
       return toString("");
   }

   /** Constructs a check object, call by the sleep engine. */
   public CheckEval(String n, Block s)
   {
      if (n.charAt(0) == '!' && n.length() > 2) // negation operator - we don't apply it though for like != or any other 2 character operator
      {
         name   = n.substring(1, n.length());
         negate = true;
      }
      else
      {
         name = n;
         negate = false;
      }
      setup = s;

      iftrue = null;
      iffalse = null;
   }

   private int hint = -1;

   /** Sets the line number in code where this check object occurs, again called by the sleep engine */
   public void setInfo(int _hint)
   {
      hint = _hint;
   }

   /** Performs this "check".  Returns the value of the condition that is checked. */
   public boolean check(ScriptEnvironment env)
   {
      env.CreateFrame();
      setup.evaluate(env);
      Predicate choice = env.getPredicate(name);
 
      boolean temp;

      if (choice == null)
      {
         env.getScriptInstance().fireWarning("Attempted to use non-existent predicate: " + name, hint);
         temp = false;
      }
      else
      {
         if ((env.getScriptInstance().getDebugFlags() & ScriptInstance.DEBUG_TRACE_LOGIC) == ScriptInstance.DEBUG_TRACE_LOGIC)
         {
            StringBuffer message = new StringBuffer(64);
            if (env.getCurrentFrame().size() >= 2)
            {
               message.append(SleepUtils.describe((Scalar)env.getCurrentFrame().get(0)));
               message.append(" "); 
               if (negate) { message.append("!"); }
               message.append(name);
               message.append(" ");
               message.append(SleepUtils.describe((Scalar)env.getCurrentFrame().get(1)));
            }
            else if (env.getCurrentFrame().size() == 1)
            {
               if (negate) { message.append("!"); }
               message.append(name);
               message.append(" ");
               message.append(SleepUtils.describe((Scalar)env.getCurrentFrame().get(0)));
            }
            else
            {
               message.append("corrupted stack frame: " + name);
            }
            temp = choice.decide(name, env.getScriptInstance(), env.getCurrentFrame());
            message.append(" ? ");

            if (negate)
            {
               message.append((!temp + "").toUpperCase());
            }
            else
            {
               message.append((temp + "").toUpperCase());
            }

            env.getScriptInstance().fireWarning(message.toString(), hint, true);
         }
         else
         {
            temp = choice.decide(name, env.getScriptInstance(), env.getCurrentFrame());
         }
      }

      env.KillFrame();

      if (negate) { temp = !temp; }

      return temp;
   }
}



