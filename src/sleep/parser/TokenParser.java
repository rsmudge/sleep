/* 
   SLEEP - Simple Language for Environment Extension Purposes 
 .--------------------------.
 | sleep.parser.TokenParser |_________________________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: parser for the SLEEP language.  Returns a sleep.engine.Block
     object suitable for being passed to the interpreter for execution.  Hell
     if you were really creative you could serialize the Block object so you
     don't have to reparse the script each time you load SLEEP.

   Documentation: To see the entire concrete syntax of the SLEEP language
     handled by this parser view the file docs/bnf.txt.

   Changes:

   * This software is distributed under the artistic license, see license.txt
     for more information. *
   
 |____________________________________________________________________________|
 */

package sleep.parser;

import java.util.*;

public class TokenParser implements ParserConstants
{
   public static Statement ParseObject (Parser parser, TokenList data)
   {
      Token[]  tokens  = data.getTokens();
      String[] strings = data.getStrings();

      Statement myToken = new Statement();

      int idx = 0;

      if (tokens.length < 1)
      {
         parser.reportError("Object Access: expression is empty", new Token(data.toString(), tokens[0].getHint()));
         return null;
      }

      if (strings.length >= 2 && strings[1].equals(".")) // if we are specifying a literal classname, lets rebuild all of this...
      {
         StringBuffer token = new StringBuffer();
         int          t;

         for (t = 0; t < (tokens.length - 1) && strings[t+1].equals("."); t += 2)
         {
            token.append(strings[t]);
            token.append(".");
         }

         if (t < tokens.length)
         {
            token.append(strings[t]);
            t++;
         }

         Token     nToken = tokens[0].copy(token.toString());
         TokenList nList  = new TokenList();

         nList.add(nToken);
                
         while (t < tokens.length)
         {
            nList.add(tokens[t]);
            t++;
         }
 
         return ParseObject(parser, nList);         
      }
      else if (strings.length >= 3 && strings[2].equals(".")) // if we are specifying a literal classname, lets rebuild all of this...
      {
         StringBuffer token = new StringBuffer();
         int          t;

         TokenList nList  = new TokenList();
         nList.add(tokens[0]);

         for (t = 1; t < (tokens.length - 1) && strings[t+1].equals("."); t += 2)
         {
            token.append(strings[t]);
            token.append(".");
         }

         if (t < tokens.length)
         {
            token.append(strings[t]);
            t++;
         }

         Token     nToken = tokens[1].copy(token.toString());

         nList.add(nToken);
                
         while (t < tokens.length)
         {
            nList.add(tokens[t]);
            t++;
         }
 
         return ParseObject(parser, nList);         
      }

      if (strings.length == 1)
      {
         myToken.setType(OBJECT_CL_CALL);
         myToken.add(tokens[0]);

         idx = 1;
      }
      else if (strings.length >= 2 && Checkers.isClosureCall(strings[0], strings[1]))
      {
         myToken.setType(OBJECT_CL_CALL);
         myToken.add(tokens[0]);

         idx = 1;         
      }
      else if (strings.length >= 2 && Checkers.isObjectNew(strings[0], strings[1]))
      {
         myToken.setType(OBJECT_NEW);
         myToken.add(tokens[1]);
 
         idx = 2;
      }
      else if (strings.length >= 2 && Checkers.isClassIdentifier(parser, strings[0]))
      {
         myToken.setType(OBJECT_ACCESS_S);
         myToken.add(tokens[0]);
         myToken.add(tokens[1]);

         idx = 2;
      }
      else
      {
         myToken.setType(OBJECT_ACCESS);
         myToken.add(tokens[0]);
         myToken.add(tokens[1]);

         idx = 2;
      }

      if (idx >= tokens.length)
      {
         return myToken;
      }

      if (!strings[idx].equals("EOT"))
      {
         parser.reportError("Object Access: parameter separator is :", new Token(data.toString(), tokens[0].getHint()));
         return null;
      }
      else if ((idx + 1) >= tokens.length)
      {
         parser.reportError("Object Access: can not specify empty arg list after :", new Token("[" + data.toString().substring(0, data.toString().length() - 4) + ":<null>]", tokens[0].getHint()));
         return null;
      }

      idx++;

      StringBuffer temp = new StringBuffer(strings[idx]);
      idx++;

      while (idx < tokens.length)
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

      boolean is_or = false, is_and = false;

      int x = 0; // below you'll see me comparing indexes relative to this x variable,
                 // this is a left over thing from when I had a needless for loop here.

      if (tokens.length >= 3 && Checkers.isUniPredicate(strings[x], strings[x+1]) && Checkers.isIndexableItem(strings[x+1], strings[x+2]))
      {
          myToken.add(tokens[x]);
          x++;
          myToken.add(ParserUtilities.makeToken(strings[x] + strings[x+1], tokens[0]));
          x += 2;
          while (x < tokens.length)
          {
             myToken.add(tokens[x]);
             x++;
          }

          return ParsePredicate(parser, myToken);
      }
      else if (tokens.length == 3)
      {
         is_or  =  Checkers.isOrPredicate(strings[x], strings[x+1], strings[x+2]);
         is_and = Checkers.isAndPredicate(strings[x], strings[x+1], strings[x+2]);
      }
      else if (2 < tokens.length)
      {
         int check = findPrecedentOperators(myToken, data, 0, "&& ||", 2); // simple precedence hack

         if (check != 0) // we found a precedent operator... wheee...
         {
            return ParsePredicate(parser, myToken);
         }
      }

      // this break is intentional, the if series above checks for an &&, || operator situation
 
      if ((x + 2) < tokens.length && (is_or || is_and))
      {
         if (is_or)
            myToken.setType(PRED_OR);

         if (is_and)
            myToken.setType(PRED_AND);

         myToken.add(tokens[x]);
         myToken.add(tokens[x+1]);
         x += 2;
      }
      else if ((x + 2) < tokens.length && Checkers.isBiPredicate(strings[x], strings[x+1], strings[x+2]))
      {
         myToken.setType(PRED_BI);
         myToken.add(tokens[x]);
         myToken.add(tokens[x+1]);
         x += 2;
      }
      else if ((x + 1) < tokens.length && Checkers.isUniPredicate(strings[x], strings[x+1]))
      {
         myToken.setType(PRED_UNI);
         myToken.add(tokens[x]);
         x++;
      }
      else if (Checkers.isExpression(strings[x]))
      {
         myToken.setType(PRED_EXPR);
      }
      else
      {
         myToken.setType(PRED_IDEA);
      }
 
      if (x < tokens.length)
      {
         StringBuffer temp = new StringBuffer(strings[x]);
         x++;

         while (x < tokens.length)
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

       for (int x = start; x < tokens.length; x++)
       {
           if (strings[x].equals("EOT")) // these are not the droids you are looking for,
           {                             // see if we encounter an End of Term before our searched for operators
               return start;
           }
           else if (strings[x].length() == osize && operators.indexOf(strings[x]) > -1)
           {
               // calculate left hand side...
               StringBuffer lhs = new StringBuffer(strings[start]);
               for (int l = start + 1; l < x; l++)
               {
                  lhs.append(" ");
                  lhs.append(strings[l]);
               }

               // calculate right hand side
               StringBuffer rhs = new StringBuffer(strings[x + 1]);

               int r;
               for (r = x + 2; r < tokens.length && !strings[r].equals("EOT"); r++)
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

   public static LinkedList ParseIdea(Parser parser, TokenList data)
   {
      Token[]  tokens  = data.getTokens();
      String[] strings = data.getStrings();

      LinkedList value = new LinkedList();

      Statement myToken;

      for (int x = 0; x < tokens.length; x++)
      {
         myToken = new Statement();

         if ((x + 2) < tokens.length && Checkers.isOperator(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(IDEA_OPER);

            int check = findPrecedentOperators(myToken, data, x, "=>", 2); // highest precedence: identifier => value pair

            if (check != x) // we found a precedent operator... wheee...
            {
               myToken.setType(IDEA_HASH_PAIR);
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
                  StringBuffer otherTerms = new StringBuffer(strings[x+2]);
                  x += 3;
                  while ((x < tokens.length) && !strings[x].equals("EOT"))
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
            myToken.setType(VALUE_INDEXED);

            Token[] temp  = LexicalAnalyzer.CreateTerms(parser, new StringIterator(strings[x], tokens[x].getHint())).getTokens();

            int z = 0;

            if ((z + 1) < temp.length && Checkers.isFunctionCall(temp[0].toString(), temp[1].toString()))
            {
               myToken.add(ParserUtilities.combineTokens(temp[0], temp[1]));
               z += 2;
            }

            while (z < temp.length)
            {
               myToken.add(temp[z]);
               z++;
            }
         }
         else if (Checkers.isIndex(strings[x]))
         {
            myToken.setType(IDEA_EXPR_I);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isFunctionCall(strings[x])) /* moved above isVariable since @() and %() are now "function" calls */
         {
            myToken.setType(IDEA_FUNC);
            myToken.add(tokens[x]);
         }
         // increment hack
         else if (Checkers.isIncrementHack(strings[x]))
         {
            myToken.setType(HACK_INC);
            myToken.add(tokens[x]);
         }
         // decrement hack
         else if (Checkers.isDecrementHack(strings[x]))
         {
            myToken.setType(HACK_DEC);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isVariableReference(strings[x]))
         {
            myToken.setType(VALUE_SCALAR_REFERENCE);
            myToken.add(tokens[x]);
         }
         // a normal block
         else if (Checkers.isVariable(strings[x]))
         {
            myToken.setType(VALUE_SCALAR);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isExpression(strings[x]))
         {
            myToken.setType(IDEA_EXPR);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isFunction(strings[x]) && Checkers.isFunctionReferenceToken(strings[x]))
         {
            myToken.setType(IDEA_FUNC);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isString(strings[x]))
         {
            myToken.setType(IDEA_STRING);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isBacktick(strings[x]))
         {
            myToken.setType(EXPR_EVAL_STRING);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isLiteral(strings[x]))
         {
            myToken.setType(IDEA_LITERAL);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isNumber(strings[x]))
         {
            myToken.setType(IDEA_NUMBER);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isDouble(strings[x]))
         {
            myToken.setType(IDEA_DOUBLE);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isBoolean(strings[x]))
         {
            myToken.setType(IDEA_BOOLEAN);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isBlock(strings[x]))
         {
            myToken.setType(IDEA_BLOCK);
            myToken.add(tokens[x]);
         }
         else if (Checkers.isClassLiteral(strings[x]))
         {
            myToken.setType(IDEA_CLASS);
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

   public static LinkedList ParseBlocks(Parser parser, TokenList data)
   {
      String[]  strings = data.getStrings();
      Token[]   tokens  = data.getTokens();

      LinkedList value  = new LinkedList();

      Statement myToken;
      int check;

      for (int x = 0; x < tokens.length; x++)
      {
    	 myToken = new Statement();

         // a foreach loop
         if ((x + 5) < tokens.length && Checkers.isSpecialForeach(strings[x], strings[x+1], strings[x+2], strings[x+3], strings[x+4], strings[x+5]))
         {
            myToken.setType(EXPR_FOREACH_SPECIAL);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            myToken.add(tokens[x+4]);
            myToken.add(tokens[x+5]);
            x += 5;
         }
         else if ((x + 4) < tokens.length && Checkers.isTryCatch(strings[x], strings[x+1], strings[x+2], strings[x+3], strings[x+4]))
         {
            myToken.setType(EXPR_TRYCATCH);
            myToken.add(tokens[x]); 
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            myToken.add(tokens[x+4]);
            x += 4;
         }
         else if ((x + 3) < tokens.length && Checkers.isSpecialWhile(strings[x], strings[x+1], strings[x+2], strings[x+3]))
         {
            myToken.setType(EXPR_WHILE_SPECIAL);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            x += 3;
         }
         else if ((x + 3) < tokens.length && Checkers.isForeach(strings[x], strings[x+1], strings[x+2], strings[x+3]))
         {
            myToken.setType(EXPR_FOREACH);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            myToken.add(tokens[x+3]);
            x += 3;
         }
         // parse if-else if-else blocks
         else if (((x + 2) < tokens.length) && Checkers.isIfStatement(strings[x], strings[x+1], strings[x+2]))
	 {
            myToken.setType(EXPR_IF_ELSE);
   	    myToken.add(tokens[x]);
	    myToken.add(tokens[x+1]);
	    myToken.add(tokens[x+2]);
	    x += 3;
            while ((x + 3) < tokens.length && Checkers.isElseIfStatement(strings[x], strings[x+1], strings[x+2], strings[x+3]))
	    {
               myToken.add(tokens[x]);
               myToken.add(tokens[x+1]);
               myToken.add(tokens[x+2]);
               myToken.add(tokens[x+3]);
               x += 4;
	    }
            if ((x + 1) < tokens.length && Checkers.isElseStatement(strings[x], strings[x+1]))
	    {
               myToken.add(tokens[x]);
	       myToken.add(tokens[x+1]);
               x += 2;
	    }
	    x--; /* this is only need in the if nesting tokenizer thingy. */
	 }
         // a while loop
         else if ((x + 2) < tokens.length && Checkers.isWhile(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(EXPR_WHILE);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            x += 2;
         }
         // a for loop
         else if ((x + 2) < tokens.length && Checkers.isFor(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(EXPR_FOR);
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
               myToken.setType(EXPR_ASSERT);
            }
            else
            {
               myToken.setType(EXPR_RETURN);
            }
            myToken.add(tokens[x]);
           
            x++;

            StringBuffer newExpr = new StringBuffer();

            if (x == tokens.length) // if return is the only token, that means we have no EOT, ergo an error
            {
               parser.reportError("Missing terminator", new Token(newExpr.toString(), tokens[x - 1].getHint(), newExpr.toString().length()));
               return null;
            }
           
            int hint = tokens[x].getHint();

            /* keep looping until we reach an end of term clause */
            while (x < strings.length && !strings[x].equals("EOT"))
            {
               newExpr.append(strings[x]);
               newExpr.append(" ");
               x++;

               if (x >= tokens.length)
               {
                  parser.reportError("Missing terminator", new Token(newExpr.toString(), tokens[x - 1].getHint(), newExpr.toString().length()));
                  return null;
               }
            }

            if (newExpr.length() > 0)
               myToken.add(new Token(newExpr.toString(), hint));
         }
         // import statement :)
         else if ( (x + 1) < tokens.length && Checkers.isImportStatement(strings[x], strings[x+1])  )
         {
            myToken.setType(OBJECT_IMPORT);

            x++;

            StringBuffer newExpr = new StringBuffer();

            /* keep looping until we reach an end of term clause */
            while (x < strings.length && !strings[x].equals("EOT"))
            {
               if (strings[x].equals("from:"))
               {
                  if (newExpr.length() == 0)
                  {
                     parser.reportError("Attempted to import '' from:", new Token("import from:", tokens[x].getHint(), "import from:".length()));
                     return null;
                  }
                  else
                  {
                     myToken.add(new Token(newExpr.toString(), tokens[x].getHint()));
                     newExpr = new StringBuffer();
                  }
               }
               else
               {
                  newExpr.append(strings[x]);
               }

               x++;

               if (x >= tokens.length)
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
            myToken.setType(IDEA_EXPR_I);
            myToken.add(tokens[x]);
         }
         else if (strings[x].equals("EOT")) /* do this before the binding stuff to prevent problems, k */
         {
            // do nothing.
         }
         // a bind predicate structure
         else if ((x + 2) < tokens.length && Checkers.isBindPredicate(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(EXPR_BIND_PRED);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            x += 2;
         }
         // a bind structure
         else if ((x + 2) < tokens.length && Checkers.isBind(strings[x], strings[x+1], strings[x+2]))
         {
            myToken.setType(EXPR_BIND);
            myToken.add(tokens[x]);
            myToken.add(tokens[x+1]);
            myToken.add(tokens[x+2]);
            x += 2;
         }
         // increment hack
         else if (Checkers.isIncrementHack(strings[x]))
         {
            myToken.setType(HACK_INC);
            myToken.add(tokens[x]);
         }
         // decrement hack
         else if (Checkers.isDecrementHack(strings[x]))
         {
            myToken.setType(HACK_DEC);
            myToken.add(tokens[x]);
         }
         // a normal block
         else if (Checkers.isBlock(strings[x]))
         {
            myToken.setType(EXPR_BLOCK);
            myToken.add(tokens[x]);
         }
         // a backtick evaluation ...
         else if (Checkers.isBacktick(strings[x]))
         {
            myToken.setType(EXPR_EVAL_STRING);
            myToken.add(tokens[x]);
         }
         // a function call
         else if (Checkers.isFunctionCall(strings[x]))
         {
            myToken.setType(IDEA_FUNC);
            myToken.add(tokens[x]);
         }
         // a bind filter structure
         else if ((x + 3) < tokens.length && Checkers.isBindFilter(strings[x], strings[x+1], strings[x+2], strings[x+3]))
         {
            myToken.setType(EXPR_BIND_FILTER);
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
               myToken.setType(EXPR_ASSIGNMENT_T_OP);
            }
            else
            {
               myToken.setType(EXPR_ASSIGNMENT_OP);
            }

            x = check;
         }
         else if ((check = findPrecedentOperators(myToken, data, x, "=", 1)) != x) // checking for a assignment :)
         {
            if (Checkers.isExpression(strings[x]))
            {
               myToken.setType(EXPR_ASSIGNMENT_T);
            }
            else
            {
               myToken.setType(EXPR_ASSIGNMENT);
            }

            x = check;
         }
         else
         {
            TokenList alist = new TokenList();
            for (int z = x; z < tokens.length && !strings[z].equals("EOT") && strings[z].indexOf('\n') == -1; z++)
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

