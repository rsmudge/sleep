/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------.
 | sleep.engine.atoms.Bind |__________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/

   Description: This class contains an implementation of an atomic Step for
     the sleep scripting.  

   Documentation:

   Changelog:
   11/17/2002 - this class was refactored out of Step and put in its own file.

   * This software is distributed under the artistic license, see license.txt
     for more information. *

 |____________________________________________________________________________|
 */

package sleep.engine.atoms;

import java.util.*;
import sleep.interfaces.*;
import sleep.engine.*;
import sleep.runtime.*;

public class BindPredicate extends Step
{
   String funcenv;
   Check pred;
   Block code;
 
   public String toString()
   {
      StringBuffer temp = new StringBuffer();
      temp.append("[Bind Predicate]: \n");
      temp.append("   [Pred]:       \n");
      temp.append(pred.toString("      "));
      temp.append("   [Code]:       \n");
      temp.append(code.toString("      "));

      return temp.toString();
   }

   public BindPredicate(String e, Check p, Block c)
   {
      funcenv = e;
      pred = p;
      code = c;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      PredicateEnvironment temp = e.getPredicateEnvironment(funcenv);
      
      if (temp != null)
      {
         temp.bindPredicate(e.getScriptInstance(), funcenv, pred, code);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to bind code to non-existent predicate environment: " + funcenv, getLineNumber());
      }

      return null;
   }
}



