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

public class BindFilter extends Step
{
   String funcenv;
   Block  code;
   String filter;
   String name;
 
   public String toString()
   {
      StringBuffer temp = new StringBuffer();
      temp.append("[Bind Filter]: "+name+"\n");
      temp.append("   [Filter]:       \n");
      temp.append("      " + filter.toString());
      temp.append("   [Code]:       \n");
      temp.append(code.toString("      "));

      return temp.toString();
   }

   public BindFilter(String e, String n, Block c, String f)
   {
      funcenv = e;
      code    = c;
      filter  = f;
      name    = n;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      FilterEnvironment temp = e.getFilterEnvironment(funcenv);
      
      if (temp != null)
      {
         temp.bindFilteredFunction(e.getScriptInstance(), funcenv, name, filter, code);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to bind code to non-existent predicate environment: " + funcenv, getLineNumber());
      }

      return null;
   }
}



