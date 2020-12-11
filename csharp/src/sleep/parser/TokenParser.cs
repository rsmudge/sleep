/*
 * Copyright 2002-2020 Raphael Mudge
 * Copyright 2020 Sebastian Ritter
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 *    of conditions and the following disclaimer in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without specific prior
 *    written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
using System;
using java = biz.ritter.javapi;

namespace sleep.parser{


public class TokenParser : ParserConstants
{
   public static Statement ParseObject (Parser parser, TokenList data)
   {
      Token[]  tokens  = data.getTokens();
      String[] strings = data.getStrings();

      Statement myToken = new Statement();

      int idx = 0;

      if (tokens.Length < 1)
      {
         parser.reportError("Object Access: expression is empty", new Token(data.toString(), tokens[0].getHint()));
         return null;
      }

      if (strings.Length >= 2 && strings[1].equals(".")) // if we are specifying a literal classname, lets rebuild all of this...
      {
         java.lang.StringBuffer token = new java.lang.StringBuffer();
         int          t;

         for (t = 0; t < (tokens.Length - 1) && strings[t+1].equals("."); t += 2)
         {
            token.append(strings[t]);
            token.append(".");
         }

         if (t < tokens.Length)
         {
            token.append(strings[t]);
            t++;
         }

         Token     nToken = tokens[0].copy(token.toString());
         TokenList nList  = new TokenList();

         nList.add(nToken);
                
         while (t < tokens.Length)
         {
            nList.add(tokens[t]);
            t++;
         }
 
         return ParseObject(parser, nList);         
      }
      else if (strings.Length >= 3 && strings[2].equals(".")) // if we are specifying a literal classname, lets rebuild all of this...
      {
         java.lang.StringBuffer token = new java.lang.StringBuffer();
         int          t;

         TokenList nList  = new TokenList();
         nList.add(tokens[0]);

         for (t = 1; t < (tokens.Length - 1) && strings[t+1].equals("."); t += 2)
         {
            token.append(strings[t]);
            token.append(".");
         }

         if (t < tokens.Length)
         {
            token.append(strings[t]);
            t++;
         }

         Token     nToken = tokens[1].copy(token.toString());

         nList.add(nToken);
                
         while (t < tokens.Length)
         {
            nList.add(tokens[t]);
            t++;
         }
 
         return ParseObject(parser, nList);         
      }

      if (strings.Length == 1)
      {
         myToken.setType(ParserConstants.OBJECT_CL_CALL);
         myToken.add(tokens[0]);

         idx = 1;
      }
      else if (strings.Length >= 2 && Checkers.isClosureCall(strings[0], strings[1]))
      {
         myToken.setType(ParserConstants.OBJECT_CL_CALL);
         myToken.add(tokens[0]);

         idx = 1;         
      }
      else if (strings.Length >= 2 && Checkers.isObjectNew(strings[0], strings[1]))
      {
         myToken.setType(ParserConstants.OBJECT_NEW);
         myToken.add(tokens[1]);
 
         idx = 2;
      }
      else if (strings.Length >= 2 && Checkers.isClassIdentifier(parser, strings[0]))
      {
         myToken.setType(ParserConstants.OBJECT_ACCESS_S);
         myToken.add(tokens[0]);
         myToken.add(tokens[1]);

         idx = 2;
      }
      else
      {
         myToken.setType(ParserConstants.OBJECT_ACCESS);
         myToken.add(tokens[0]);
         myToken.add(tokens[1]);

         idx = 2;
      }

      if (idx >= tokens.Length)
      {
         return myToken;
      }

      if (!strings[idx].equals("EOT"))
      {
         parser.reportError("Object Access: parameter separator is :", new Token(data.toString(), tokens[0].getHint()));
         return null;
      }
      else if ((idx + 1) >= tokens.Length)
      {
         parser.reportError("Object Access: can not specify empty arg list after :", new Token("[" + data.toString().substring(0, data.toString().length() - 4) + ":<null>]", tokens[0].getHint()));
         return null;
      }

      idx++;

      java.lang.StringBuffer temp = new java.lang.StringBuffer(strings[idx]);
      idx++;

      while (idx < tokens.Length)
      {
         temp.append(" ");
         temp.append(strings[idx]);
         idx++;
      }

      myToken.add(new Token(temp.toString(), tokens[idx-1].getHint()));
      return myToken;
   }

   public static Statement ParsePredicate (Parser parser, TokenList data)
   {
      Token[]  tokens  = data.getTokens();
      String[] strings = data.getStrings();

      Statement myToken = new Statement();

      bool is_or = false, is_and = false;

      int x = 0; // below you'll see me comparing indexes relative to this x variable,
                 // this is a left over thing from when I had a needless for loop here.

      if (tokens.Length >= 3 && Checkers.isUniPredicate(strings[x], strings[x+1]) && Checkers.isIndexableItem(strings[x+1], strings[x+2]))
      {
          myToken.add(tokens[x]);
          x++;
          myToken.add(ParserUtilities.makeToken(strings[x] + strings[x+1], tokens[0]));
          x += 2;
          while (x < tokens.Length)
          {
             myToken.add(tokens[x]);
             x++;
          }

          return ParsePredicate(parser, myToken);
      }
      else if (tokens.Length == 3)
      {
         is_or  =  Checkers.isOrPredicate(strings[x], strings[x+1], strings[x+2]);
         is_and = Checkers.isAndPredicate(strings[x], strings[x+1], strings[x+2]);
      }
      else if (2 < tokens.Length)
      {
         int check = findPrecedentOperators(myToken, data, 0, "&& ||", 2); // simple precedence hack

         if (check != 0) // we found a precedent operator... wheee...
         {
            return ParsePredicate(parser, myToken);
         }
      }

      // this break is intentional, the if series above checks for an &&, || operator situation
 
      if ((x + 2) < tokens.Length && (is_or || is_and))
      {
         if (is_or)
            myToken.setType(ParserConstants.PRED_OR);

         if (is_and)
            myToken.setType(ParserConstants.PRED_AND);

         myToken.add(tokens[x]);
         myToken.add(tokens[x+2]);
         x += 3;
      }
      else if ((x + 2) < tokens.Length && Checkers.isBiPredicate(strings[x], strings[x+1], strings[x+2]))
      {
         myToken.setType(ParserConstants.PRED_BI);
         myToken.add(tokens[x]);
         myToken.add(tokens[x+1]);
         x += 2;
      }
      else if ((x + 1) < tokens.Length && Checkers.isUniPredicate(strings[x], strings[x+1]))
      {
         myToken.setType(ParserConstants.PRED_UNI);
         myToken.add(tokens[x]);
         x++;
      }
      else if (Checkers.isExpression(strings[x]))
      {
         myToken.setType(ParserConstants.PRED_EXPR);
      }
      else
      {
         myToken.setType(ParserConstants.PRED_IDEA);
      }
 
      if (x < tokens.Length)
      {
         java.lang.StringBuffer temp = new java.lang.StringBuffer(strings[x]);
         x++;

         while (x < tokens.Length)
         {
           temp.append(" ");
           temp.append(strings[x]);
           x++;
         }
            
         myToken.add(new Token(temp.toString(), tokens[x-1].getHint()));
      }

      return myToken;
   }

   //
   // Simple Precedence Hack:
   // This function scans a statement that is a string of operators for some low precedence operators.
   // If it finds them it reassembles the string based around these operators getting evaluated last.
   // For now we just have +, -, and . explicitly taking less precedence than other user added operators.   
   //
   // With this in place I think precedence should be reasonably correct.
   //
   protected static int findPrecedentOperators(Statement statement, TokenList data, int start, String operators, int osize)
   {
       String[] strings = data.getStrings();
       Token[]  tokens  = data.getTokens();

       for (int x = start; x < tokens.Length; x++)
       {
           if (strings[x].equals("EOT")) // these are not the droids you are looking for,
           {                             // see if we encounter an End of Term before our searched for operators
               return start;
           }
           else if (strings[x].length() == osize && operators.indexOf(strings[x]) > -1)
           {
               // calculate left hand side...
               java.lang.StringBuffer lhs = new java.lang.StringBuffer(strings[start]);
               for (int l = start + 1; l < x; l++)
               {
                  lhs.append(" ");
                  lhs.append(strings[l]);
               }

               // calculate right hand side
               java.lang.StringBuffer rhs = new java.lang.StringBuffer(strings[x + 1]);

               int r;
               for (r = x + 2; r < tokens.Length && !strings[r].equals("EOT"); r++)
               {
                  rhs.append(" ");
                  rhs.append(strings[r]);
               }
 
               // make our nice new tokens...  and ship it off.
               statement.add(new Token(lhs.toString(), tokens[start].getHint()));
               statement.add(tokens[x]);
               statement.add(new Token(rhs.toString(), tokens[x + 1].getHint()));

               return r;
           }
       }

       return start;
   }

   public static java.util.LinkedList<Object> ParseIdea(Parser parser, TokenList data)
   {
      Token[]  tokens  = data.getTokens();
      String[] strings = data.getStrings();

      java.util.LinkedList<Object> value = new java.util.LinkedList<Object>();

      Statement myToken;

      for (int x = 0; x < tokens.Length; x++)
      {
         myToken = new Statement();

         if ((x + 2) < tokens.Length && Checkers.isOperator(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(ParserConstants.IDEA_OPER);

            int check = findPrecedentOperators(myToken, data, x, "=>", 2); // highest precedence: identifier => value pair

            if (check != x) // we found a precedent operator... wheee...
            {
               myToken.setType(ParserConstants.IDEA_HASH_PAIR);
               x = check;
            }
            else
            {
               check = findPrecedentOperators(myToken, data, x, "+ - .", 1); // simple precedence hack

               if (check == x) // if check is == to x then we didn't find any operators with a higher precedent.
               {
                  myToken.add(tokens[x]);
                  myToken.add(tokens[x+1]);
  
                  int hint = tokens[x+2].getHint();
                  java.lang.StringBuffer otherTerms = new java.lang.StringBuffer(strings[x+2]);
                  x += 3;
                  while ((x < tokens.Length) && !strings[x].equals("EOT"))
                  {
                     otherTerms.append(" ");
                     otherTerms.append(strings[x]);
                     x++;
                  }
                  myToken.add(new Token(otherTerms.toString(), hint));
               }
               else
               {
                  x = check; // since we did find our precedent operators, set x to be where the precedence check ended
               }
            }
         }
         else if (Checkers.isIndexableItem(strings[x]))
         {
            myToken.setType(ParserConstants.VALUE_INDEXED);

            Token[] temp  = LexicalAnalyzer.CreateTerms(parser, new StringIterator(strings[x], tokens[x].getHint())).getTokens();

            int z = 0;

            if ((z + 1) < temp.Length && Checkers.isFunctionCall(temp[0].toString(), temp[1].toString()))
            {
               myToken.add(ParserUtilities.combineTokens(temp[0], temp[1]));
               z += 2;
            }

            while (z < temp.Length)
            {
               myToken.add(temp[z]);
               z++;
            }
         }
         else if (Checkers.isIndex(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_EXPR_I);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isFunctionCall(strings[x])) /* moved above isVariable since @() and %() are now "function" calls */
         {
            myToken.setType(ParserConstants.IDEA_FUNC);
            myToken.add(tokens[x]);
         }
         // increment hack
         else if (Checkers.isIncrementHack(strings[x]))
         {
            myToken.setType(ParserConstants.HACK_INC);
            myToken.add(tokens[x]);
         }
         // decrement hack
         else if (Checkers.isDecrementHack(strings[x]))
         {
            myToken.setType(ParserConstants.HACK_DEC);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isVariableReference(strings[x]))
         {
            myToken.setType(ParserConstants.VALUE_SCALAR_REFERENCE);
            myToken.add(tokens[x]);
         }
         // a normal block
         else if (Checkers.isVariable(strings[x]))
         {
            myToken.setType(ParserConstants.VALUE_SCALAR);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isExpression(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_EXPR);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isFunction(strings[x]) && Checkers.isFunctionReferenceToken(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_FUNC);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isString(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_STRING);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isBacktick(strings[x]))
         {
            myToken.setType(ParserConstants.EXPR_EVAL_STRING);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isLiteral(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_LITERAL);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isNumber(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_NUMBER);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isDouble(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_DOUBLE);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isBoolean(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_BOOLEAN);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isBlock(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_BLOCK);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isClassLiteral(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_CLASS);
            myToken.add(tokens[x]);
         }
         else
         {
            parser.reportError("Unknown expression", new Token(data.toString(), tokens[x].getHint()));
            return null;
         }

         value.add(myToken);
      }

      return value;
   }

   public static java.util.LinkedList<Object> ParseBlocks(Parser parser, TokenList data)
   {
      String[]  strings = data.getStrings();
      Token[]   tokens  = data.getTokens();

      java.util.LinkedList<Object> value  = new java.util.LinkedList<Object>();

      Statement myToken;
      int check;

      for (int x = 0; x < tokens.Length; x++)
      {
    	 myToken = new Statement();

         // a foreach loop
         if ((x + 5) < tokens.Length && Checkers.isSpecialForeach(strings[x], strings[x+1], strings[x+2], strings[x+3], strings[x+4], strings[x+5]))
         {
            myToken.setType(ParserConstants.EXPR_FOREACH_SPECIAL);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            myToken.add(tokens[x+4]);
            myToken.add(tokens[x+5]);
            x += 5;
         }
         else if ((x + 4) < tokens.Length && Checkers.isTryCatch(strings[x], strings[x+1], strings[x+2], strings[x+3], strings[x+4]))
         {
            myToken.setType(ParserConstants.EXPR_TRYCATCH);
            myToken.add(tokens[x]); 
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            myToken.add(tokens[x+4]);
            x += 4;
         }
         else if ((x + 3) < tokens.Length && Checkers.isSpecialWhile(strings[x], strings[x+1], strings[x+2], strings[x+3]))
         {
            myToken.setType(ParserConstants.EXPR_WHILE_SPECIAL);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            x += 3;
         }
         else if ((x + 3) < tokens.Length && Checkers.isForeach(strings[x], strings[x+1], strings[x+2], strings[x+3]))
         {
            myToken.setType(ParserConstants.EXPR_FOREACH);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            x += 3;
         }
         // parse if-else if-else blocks
         else if (((x + 2) < tokens.Length) && Checkers.isIfStatement(strings[x], strings[x+1], strings[x+2]))
	 {
            myToken.setType(ParserConstants.EXPR_IF_ELSE);
   	    myToken.add(tokens[x]);
	    myToken.add(tokens[x+1]);
	    myToken.add(tokens[x+2]);
	    x += 3;
            while ((x + 3) < tokens.Length && Checkers.isElseIfStatement(strings[x], strings[x+1], strings[x+2], strings[x+3]))
	    {
               myToken.add(tokens[x]);
               myToken.add(tokens[x+1]);
               myToken.add(tokens[x+2]);
               myToken.add(tokens[x+3]);
               x += 4;
	    }
            if ((x + 1) < tokens.Length && Checkers.isElseStatement(strings[x], strings[x+1]))
	    {
               myToken.add(tokens[x]);
	       myToken.add(tokens[x+1]);
               x += 2;
	    }
	    x--; /* this is only need in the if nesting tokenizer thingy. */
	 }
         // a while loop
         else if ((x + 2) < tokens.Length && Checkers.isWhile(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(ParserConstants.EXPR_WHILE);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            x += 2;
         }
         // a for loop
         else if ((x + 2) < tokens.Length && Checkers.isFor(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(ParserConstants.EXPR_FOR);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            x += 2;
         }
         // a return statement
         else if (Checkers.isReturn(strings[x]) || Checkers.isAssert(strings[x]))
         {
            if (Checkers.isAssert(strings[x]))
            {
               myToken.setType(ParserConstants.EXPR_ASSERT);
            }
            else
            {
               myToken.setType(ParserConstants.EXPR_RETURN);
            }
            myToken.add(tokens[x]);
           
            x++;

            java.lang.StringBuffer newExpr = new java.lang.StringBuffer();

            if (x == tokens.Length) // if return is the only token, that means we have no EOT, ergo an error
            {
               parser.reportError("Missing terminator", new Token(newExpr.toString(), tokens[x - 1].getHint(), newExpr.toString().length()));
               return null;
            }
           
            int hint = tokens[x].getHint();

            /* keep looping until we reach an end of term clause */
            while (x < strings.Length && !strings[x].equals("EOT"))
            {
               newExpr.append(strings[x]);
               newExpr.append(" ");
               x++;

               if (x >= tokens.Length)
               {
                  parser.reportError("Missing terminator", new Token(newExpr.toString(), tokens[x - 1].getHint(), newExpr.toString().length()));
                  return null;
               }
            }

            if (newExpr.length() > 0)
               myToken.add(new Token(newExpr.toString(), hint));
         }
         // using  statement :)
         else if ( (x + 1) < tokens.Length && Checkers.isImportStatement(strings[x], strings[x+1])  )
         {
            myToken.setType(ParserConstants.OBJECT_IMPORT);

            x++;

            java.lang.StringBuffer newExpr = new java.lang.StringBuffer();

            /* keep looping until we reach an end of term clause */
            while (x < strings.Length && !strings[x].equals("EOT"))
            {
               if (strings[x].equals("from:"))
               {
                  if (newExpr.length() == 0)
                  {
                     parser.reportError("Attempted to using  '' from:", new Token("using  from:", tokens[x].getHint(), "using  from:".length()));
                     return null;
                  }
                  else
                  {
                     myToken.add(new Token(newExpr.toString(), tokens[x].getHint()));
                     newExpr = new java.lang.StringBuffer();
                  }
               }
               else
               {
                  newExpr.append(strings[x]);
               }

               x++;

               if (x >= tokens.Length)
               {
                  parser.reportError("Missing terminator", new Token(newExpr.toString(), tokens[x - 1].getHint(), newExpr.toString().length()));
                  return null;
               }
            }

            if (newExpr.length() > 0)
               myToken.add(new Token(newExpr.toString(), tokens[x].getHint()));
         }
         else if (Checkers.isIndex(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_EXPR_I);
            myToken.add(tokens[x]);
         }
         else if (strings[x].equals("EOT")) /* do this before the binding stuff to prevent problems, k */
         {
            // do nothing.
         }
         // a bind predicate structure
         else if ((x + 2) < tokens.Length && Checkers.isBindPredicate(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(ParserConstants.EXPR_BIND_PRED);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            x += 2;
         }
         // a bind structure
         else if ((x + 2) < tokens.Length && Checkers.isBind(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(ParserConstants.EXPR_BIND);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            x += 2;
         }
         // increment hack
         else if (Checkers.isIncrementHack(strings[x]))
         {
            myToken.setType(ParserConstants.HACK_INC);
            myToken.add(tokens[x]);
         }
         // decrement hack
         else if (Checkers.isDecrementHack(strings[x]))
         {
            myToken.setType(ParserConstants.HACK_DEC);
            myToken.add(tokens[x]);
         }
         // a normal block
         else if (Checkers.isBlock(strings[x]))
         {
            myToken.setType(ParserConstants.EXPR_BLOCK);
            myToken.add(tokens[x]);
         }
         // a backtick evaluation ...
         else if (Checkers.isBacktick(strings[x]))
         {
            myToken.setType(ParserConstants.EXPR_EVAL_STRING);
            myToken.add(tokens[x]);
         }
         // a function call
         else if (Checkers.isFunctionCall(strings[x]))
         {
            myToken.setType(ParserConstants.IDEA_FUNC);
            myToken.add(tokens[x]);
         }
         // a bind filter structure
         else if ((x + 3) < tokens.Length && Checkers.isBindFilter(strings[x], strings[x+1], strings[x+2], strings[x+3]))
         {
            myToken.setType(ParserConstants.EXPR_BIND_FILTER);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            x += 3;
         }
         else if ((check = findPrecedentOperators(myToken, data, x, "+= -= *= .= /= %= |= &= ^=", 2)) != x ||
                  (check = findPrecedentOperators(myToken, data, x, "<<= >>= **=", 3)) != x) // checking for a assignment :)
         {
            if (Checkers.isExpression(strings[x]))
            {
               myToken.setType(ParserConstants.EXPR_ASSIGNMENT_T_OP);
            }
            else
            {
               myToken.setType(ParserConstants.EXPR_ASSIGNMENT_OP);
            }

            x = check;
         }
         else if ((check = findPrecedentOperators(myToken, data, x, "=", 1)) != x) // checking for a assignment :)
         {
            if (Checkers.isExpression(strings[x]))
            {
               myToken.setType(ParserConstants.EXPR_ASSIGNMENT_T);
            }
            else
            {
               myToken.setType(ParserConstants.EXPR_ASSIGNMENT);
            }

            x = check;
         }
         else
         {
            TokenList alist = new TokenList();
            for (int z = x; z < tokens.Length && !strings[z].equals("EOT") && strings[z].indexOf('\n') == -1; z++)
            {
               alist.add(tokens[z]);
            }

            parser.reportError("Syntax error", new Token(alist.toString(), tokens[x].getHint()));
            return null;
         }

         if (myToken.getType() != 0)
         {
            value.add(myToken);
         }
      }

      return value;
   }
}

}