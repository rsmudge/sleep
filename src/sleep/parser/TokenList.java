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
