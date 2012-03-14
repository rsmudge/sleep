/* 
   SLEEP - Simple Language for Environment Extension Purposes 
 .-------------------------.
 | sleep.parser.util.Token |__________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: 

   Documentation: To see the entire concrete syntax of the SLEEP language
     handled by this parser view the file docs/bnf.txt.

   Changes:

   * This software is distributed under the artistic license, see license.txt
     for more information. *
   
 |____________________________________________________________________________|
 */

package sleep.parser;

/** as much as possible this is a String with a line number associate with it (aka hint) */
public class Token
{
   protected String term;
   protected int    hint;
   protected int    marker;
   protected int    tophint;
 
   public Token(String term, int hint)
   {
      this(term, hint, -1);
   }

   public Token(String _term, int _hint, int _marker)
   { 
      term   = _term;
      hint   = _hint;
      marker = _marker;
      tophint = -1;
   }

   public String toString()
   {
      return term;
   }

   public int getMarkerIndex()
   {
      return marker;
   }

   public Token copy(int _hint)
   {
      return new Token(term, _hint);
   }
 
   public Token copy(String text)
   {
      return new Token(text, getHint());
   }

   public String getMarker()
   {
      if (marker > -1)
      {
         StringBuffer temp = new StringBuffer();
         for (int x = 0; x < (marker - 1); x++)
         {
            temp.append(" ");
         }
         temp.append("^");

         return temp.toString();
      }

      return null;
   }

   public int getTopHint()
   {
      if (tophint >= 0) {
           return tophint;
      }

      tophint = hint;
      int endAt = -1;
      while ((endAt = term.indexOf('\n', endAt + 1)) > -1)
      {
         tophint++;
      }

      return tophint;
   }

   public int getHint()
   {
      return hint;
   }
}
