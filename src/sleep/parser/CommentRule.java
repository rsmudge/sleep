/* 
   SLEEP - Simple Language for Environment Extension Purposes 
 .------------------------.
 | sleep.parser.util.Rule |___________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: Data structure for a lexical parser rule... ooh.

   Documentation: To see the entire concrete syntax of the SLEEP language
     handled by this parser view the file docs/bnf.txt.

   Changes:

   * This software is distributed under the artistic license, see license.txt
     for more information. *
   
 |____________________________________________________________________________|
 */

package sleep.parser;

import java.util.*;
import java.io.*;

import sleep.error.*;

public class CommentRule extends Rule
{
   public int getType() { return PRESERVE_SINGLE; }
    
   public String toString()
   {
      return "Comment parsing information";
   }

   public String wrap(String value)
   {
      StringBuffer rv = new StringBuffer(value.length() + 2);
      rv.append('#');
      rv.append(value);
      rv.append('\n');

      return rv.toString();
   }

   public boolean isLeft(char n) { return (n == '#'); }
   public boolean isRight(char n) { return (n == '\n'); }
   public boolean isMatch(char n) { return false; }

   public boolean isBalanced()
   {
      return true;
   }

   public Rule copyRule()
   {
      return this;  // we're safe doing this since comment rules contain no state information.
   }

   /** Used to keep track of opening braces to check balance later on */
   public void witnessOpen(Token token) { }

   /** Used to keep track of closing braces to check balance later on */
   public void witnessClose(Token token) { }
}
