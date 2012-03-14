/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------.
 | sleep.engine.atoms.SValue |__________________________________________________
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
import sleep.bridges.*;

public class CreateClosure extends Step
{
   Block  block = null;

   public String toString(String prefix)
   {
      return prefix + "[Create Closure]\n" + block.toString(prefix + "   ");
   }

   public CreateClosure(Block _block)
   {
      block = _block;
   }

   // 
   // no stack pre condition.
   //
   // post condition:
   //   pushes closure onto current frame

   public Scalar evaluate(ScriptEnvironment e)
   {
      Scalar value = SleepUtils.getScalar(new SleepClosure(e.getScriptInstance(), block));
      e.getCurrentFrame().push(value);

      return null;
   }
}

