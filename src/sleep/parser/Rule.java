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

public class Rule
{
   public static int PRESERVE_ENTITY = 100;
   public static int PRESERVE_SINGLE = 101;

   int type;
   char left;
   char right;
   char single;

   String errorDescription1, errorDescription2;

   public int getType() { return type; }
    
   public String toString()
   {
      return errorDescription1;
   }

   public SyntaxError getSyntaxError()
   {
      while (open.size() > 0 && close.size() > 0)
      {
         open.removeLast();
         close.removeLast();
      }

      String desc;

      if (type == PRESERVE_ENTITY && open.size() > 0)
      {
         desc = errorDescription2;
      }
      else
      {
         desc = errorDescription1;
      }

      Token token;

      if (open.size() > 0)
      {
         token = (Token)open.getFirst();
      }
      else
      {
         token = (Token)close.getFirst();
      }

      open.clear();
      close.clear();

      return new SyntaxError(desc, token.toString(), token.getHint(), token.getMarker());
   }

   public String wrap(String value)
   {
      StringBuffer rv = new StringBuffer(value.length() + 2);
      if (type == PRESERVE_ENTITY)
      {
         rv.append(left);
         rv.append(value);
         rv.append(right);
      }
      else
      {
         rv.append(single);
         rv.append(value);
         rv.append(single);
      }

      return rv.toString();
   }

   public boolean isLeft(char n) { return (type == PRESERVE_ENTITY && left == n); }
   public boolean isRight(char n) { return (type == PRESERVE_ENTITY && right == n); }
   public boolean isMatch(char n) { return (type == PRESERVE_SINGLE && single == n); }

   protected LinkedList open  = new LinkedList();
   protected LinkedList close = new LinkedList();

   public boolean isBalanced()
   {
      if (open.size() == close.size())
      {
         open.clear();
         close.clear();
         return true;
      }
      return false;
   }

   /** Used to keep track of opening braces to check balance later on */
   public void witnessOpen(Token token)
   {
      open.add(token);
      adjustLists();
   }

   /** Used to keep track of closing braces to check balance later on */
   public void witnessClose(Token token)
   {
      if (type == PRESERVE_ENTITY)
      {
         close.addFirst(token);
      }
      else
      {
         close.add(token);
      }
      adjustLists();
   }

   private void adjustLists()
   {
      if (open.size() > 0 && close.size() > 0)
      {
         if (((Token)open.getLast()).getHint() == ((Token)close.getLast()).getHint())
         {
            open.removeLast();
            close.removeLast();
         }
      }
   }

   public char getLeft()
   {
      return left;
   }

   public char getRight()
   {
      return right;
   }

   public Rule copyRule()
   {
      if (type == PRESERVE_ENTITY)
         return new Rule(errorDescription1, errorDescription2, left, right);
 
      return new Rule(errorDescription1, single);
   }

   public Rule(String errorDesc1, String errorDesc2, char l, char r)
   {
      type = PRESERVE_ENTITY;
      left = l;
      right = r;

      errorDescription1 = errorDesc1;
      errorDescription2 = errorDesc2;
   }

   public Rule(String errorDesc, char s)
   {
      type = PRESERVE_SINGLE;
      single = s;

      errorDescription1 = errorDesc;
   }

   public Rule() 
   {
      // don't call me unless you really have to
   }
}
