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

public class TokenList
{
   protected LinkedList terms  = new LinkedList();
   protected String[]   sarray = null;
   protected Token[]    tarray = null;

   public void add(Token temp)
   {
      terms.add(temp);
   }

   public String toString()
   {
      StringBuffer rv = new StringBuffer();

      Iterator i = terms.iterator();
      while (i.hasNext())
      {
         rv.append(i.next().toString());
         rv.append(" ");
      }

      return rv.toString();
   }

   public LinkedList getList()
   {
      return terms;
   }

   private static final Token[]  dummyT = new Token[0];
   private static final String[] dummyS = new String[0];

   public Token[] getTokens()
   {
      if (tarray == null)
      {
         tarray = (Token[])terms.toArray(dummyT);
      }
      return tarray;
   }

   public String[] getStrings()
   {
      if (sarray == null)
      {
         Token[] temp = getTokens();
         sarray = new String[temp.length];
         for (int x = 0; x < temp.length; x++)
         {
            sarray[x] = temp[x].toString();
         }
      }
      return sarray;
   }
}
