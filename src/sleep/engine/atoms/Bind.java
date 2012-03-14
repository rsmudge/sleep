
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

public class Bind extends Step
{
   String funcenv;
   Block code, name;
 
   public String toString(String prefix)
   {
      StringBuffer temp = new StringBuffer();

      temp.append(prefix);
      temp.append("[Bind Function]: \n");

      temp.append(prefix);
      temp.append("   [Name]:       \n");

      temp.append(prefix);
      temp.append(name.toString(prefix + "      "));

      temp.append(prefix);
      temp.append("   [Code]:       \n");

      temp.append(prefix);
      temp.append(code.toString(prefix + "      "));

      return temp.toString();
   }

   public Bind(String e, Block n, Block c)
   {
      funcenv = e;
      name = n;
      code = c;
   }

   //
   // no stack pre/post conditions for this step
   //

   public Scalar evaluate(ScriptEnvironment e)
   {
      Environment temp = e.getFunctionEnvironment(funcenv);
      
      if (temp != null)
      { 
         e.CreateFrame();
            name.evaluate(e);
            Scalar funcname = (Scalar)e.getCurrentFrame().pop();
         e.KillFrame();

         temp.bindFunction(e.getScriptInstance(), funcenv, funcname.getValue().toString(), code);
      }
      else
      {
         e.getScriptInstance().fireWarning("Attempting to bind code to non-existent environment: " + funcenv, getLineNumber());
      }

      return null;
   }
}



