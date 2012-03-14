/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
