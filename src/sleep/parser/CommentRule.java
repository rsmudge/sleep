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
