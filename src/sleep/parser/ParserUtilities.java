package sleep.parser;

import java.util.*;

public class ParserUtilities
{
   public static Token combineTokens(Token a, Token b)
   {
      return new Token(a.toString() + b.toString(), a.getHint());
   }

   public static Token makeToken(String token, Token a)
   {
      return new Token(token, a.getHint());
   }

   public static Token[] get(Token[] t, int a, int b)
   {
      Token rv[] = new Token[b - a];
      for (int x = 0; x < rv.length; x++)
      {
         rv[x] = t[a + x];
      }
      return rv;
   }

   public static Token join(Token [] temp)
   {
      return join(temp, " ");
   }

   public static Token join(Token [] temp, String with)
   {
      StringBuffer rv = new StringBuffer();

      for (int x = 0; x < temp.length; x++)
      {
	 if ((x > 0 && temp[x].getHint() == temp[x-1].getTopHint()) || x == 0)
         {
            rv.append(with);
         }
         else
         {
            int difference = temp[x].getHint() - temp[x-1].getTopHint();
            for (int z = 0; z < difference; z++)
            {
               rv.append("\n");
            }
         }

         rv.append(temp[x].toString());
      }

      return new Token(rv.toString(), temp[0].getHint());
   }

   public static Token extract (Token temp)
   {
      return new Token(extract(temp.toString()), temp.getHint());
   }

   public static String extract (String temp)
   {
      return temp.substring(1, temp.length() - 1);
   }

   /** breaks down the token into sub tokens that are one "term" wide, in the case of blocks separated by ; */
   public static TokenList groupByBlockTerm(Parser parser, Token smokin)
   {
       StringIterator iterator = new StringIterator(smokin.toString(), smokin.getHint());
       TokenList      tokens   = LexicalAnalyzer.GroupBlockTokens(parser, iterator);
       return groupByTerm(tokens);
   }

   /** breaks down the token into sub tokens that are one "term" wide, in the case of messages separated by : */
   public static TokenList groupByMessageTerm(Parser parser, Token smokin)
   {
       StringIterator iterator = new StringIterator(smokin.toString(), smokin.getHint());
       TokenList      tokens   = LexicalAnalyzer.GroupExpressionIndexTokens(parser, iterator);
       return groupByTerm(tokens);
   }

   /** breaks down the token into sub tokens that are one "term" wide, a termi in the case of parameters it uses , */
   public static TokenList groupByParameterTerm(Parser parser, Token smokin)
   {
       StringIterator iterator = new StringIterator(smokin.toString(), smokin.getHint());
       TokenList      tokens   = LexicalAnalyzer.GroupParameterTokens(parser, iterator);
       return groupByTerm(tokens);
   }


   private static TokenList groupByTerm(TokenList n)
   {
       TokenList rv = new TokenList();

       if (n.getList().size() == 0)
       {
          return rv;
       }

       StringBuffer current = new StringBuffer();

       int hint = -1;

       Iterator i = n.getList().iterator();
       while (i.hasNext())
       {
          Token temp = (Token)i.next();
          hint       = hint == -1 ? temp.getHint() : hint;

          if (temp.toString().equals("EOT"))
          {
             rv.add(new Token(current.toString(), hint));
             current = new StringBuffer();
             hint    = -1; /* reset hint to prevent line # skew */
          }
          else
          {
             if (current.length() > 0)
                current.append(" ");

             current.append(temp.toString());
          }
       }

       if (current.length() > 0)
          rv.add(new Token(current.toString(), hint));

       return rv;
   }
}
