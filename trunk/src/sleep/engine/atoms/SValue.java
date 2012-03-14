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

public class SValue extends Step
{
   Scalar value;

   public String toString(String prefix)
   {
      return prefix + "[Scalar]: "+value+"\n";
   }
  
   public SValue(Scalar v)
   {
      value = v;
   }

   public Scalar evaluate(ScriptEnvironment e)
   {
      e.getCurrentFrame().push(SleepUtils.getScalar(value));
      return value;
   }
}

