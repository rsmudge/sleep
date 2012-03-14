/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------.
 | sleep.engine.atoms.Push |__________________________________________________
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

public class CreateFrame extends Step
{
   public String toString(String prefix)
   {
      return prefix + "[Create Frame]\n";
   } 

   public Scalar evaluate(ScriptEnvironment e)
   {
      e.CreateFrame();
      return null;
   }
}
