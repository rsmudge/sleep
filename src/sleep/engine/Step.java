package sleep.engine;

import java.util.*;
import sleep.interfaces.*;
import java.io.Serializable;
import sleep.runtime.*;

/** 
The root of all atomic steps.

<pre>
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------.
 | sleep.engine.Step |________________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.hick.org/~raffi/

   Description: This class is the root of all atomic steps.  Atomic steps are
       the individual entities that scripts are broken down into. 

   Documentation:

   Changelog:

   * This software is distributed under the artistic license, see license.txt
     for more information. *

 |____________________________________________________________________________| 
</pre> */


public class Step implements Serializable
{
   /** the script line number that this step was generated from */
   protected int  line;

   /** Steps act as a simple self contained linked list */
   public    Step next; 

   /** returns a string representation of this atomic step */
   public String toString(String prefix)
   {
      return prefix+"[NOP]\n";
   }
 
   /** convience method for the code generator to set the line number. */
   public void setInfo(int _line)
   {
      line = _line;
   }

   /** returns the last line number that this step is associated with (assuming it is
       associated with multiple lines */
   public int getHighLineNumber()
   {
      return getLineNumber();
   }

   /** returns the first line number that this step is associated with (assuming it is
       associated with multiple lines */
   public int getLowLineNumber()
   {
      return getLineNumber();
   }

   /** returns the line number this step is associated with */
   public int getLineNumber()
   {
      return line;
   }

   /** evaluate this atomic step. */
   public Scalar evaluate(ScriptEnvironment e) 
   {
      return SleepUtils.getEmptyScalar();
   }

   public String toString()
   {
      return toString("");
   }
}

