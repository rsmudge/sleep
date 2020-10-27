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
using System;
using java = biz.ritter.javapi;

using  sleep.interfaces;
using  sleep.engine;
using  sleep.runtime;

namespace sleep.engine.atoms{

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
[Serializable]
public class CheckEval : Check, java.io.Serializable
{
   private Check   iftrue;
   private Check   iffalse;
   private Block   setup;
   private bool negate;

   public String name; 

   /** Converts this object to a string, used by the sleep engine for constructing an AST like thing */
   public String toString(String prefix)
   {     
       java.lang.StringBuffer temp = new java.lang.StringBuffer();
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
   public bool check(ScriptEnvironment env)
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
            java.lang.StringBuffer message = new java.lang.StringBuffer(64);
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



}