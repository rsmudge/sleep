/* 
   SLEEP - Simple Language for Environment Extension Purposes 
 .------------------------------.
 | sleep.engine.LexicalAnalyzer |_____________________________________________
 |                                                                            |
   Author: Raphael Mudge (raffi@hick.org)
           http://www.hick.org/~raffi/
 
   Description: Breaks sleep code down into tokens to be handled by the parser.

   Documentation: To see the entire concrete syntax of the SLEEP language
     handled by this parser view the file docs/bnf.txt.

     If there is an error it is passed to the returned TokenGroup object.
     The only error detection handled by the lexical analyzer is primarily
     mismatched strings/objects.

   Changes:

   * This software is distributed under the artistic license, see license.txt
     for more information. *
   
 |____________________________________________________________________________|
 */

package sleep.parser;

import java.util.*;
import java.io.*;

public class LexicalAnalyzer
{
   protected static Rule PAREN_RULE, BLOCK_RULE, INDEX_RULE, DQUOTE_RULE, SQUOTE_RULE, BACKTICK_RULE, COMMENT_RULE;
   protected static char EndOfTerm;
 
   static
   {
      BLOCK_RULE     = new Rule("Mismatched Braces - missing open brace", "Mismatched Braces - missing close brace", '{', '}');
      INDEX_RULE     = new Rule("Mismatched Indices - missing open index", "Mismatched Indices - missing close index", '[', ']');
      DQUOTE_RULE    = new Rule("Runaway string", '"');
      SQUOTE_RULE    = new Rule("Runaway string", '\'');
      BACKTICK_RULE  = new Rule("Runaway string", '`');
      PAREN_RULE     = new Rule("Mismatched Parentheses - missing open paren", "Mismatched Parentheses - missing close paren", '(', ')');
      COMMENT_RULE   = new CommentRule();

      EndOfTerm = ';';
   }

   private static boolean isSkippable (Parser p, char f)
   {
      return (isWhite(f) || isEndOfTerm(p, f) || isEndOfLine(f));
   }

   private static boolean isWhite (char f)
   {
      return (f == ' ' || f == '\t');
   }

   // special function for determining if we have encountered a built in operator, and to analyze the surrounding
   // environment of the "built in" operator to resolve any ambiguity.  
   //
   // using this effectively removes the white space requirement for said built in operator.  
   private static boolean isBuiltInOperator(char f, StringBuffer aTerm, StringIterator iter)
   {
      if (f == '.' && (aTerm.length() <= 0 || !(Character.isDigit(aTerm.charAt(aTerm.length() - 1)) && aTerm.charAt(0) != '$')) && !iter.isNextChar('='))
      {
         return true;
      }

      return false;
   }

   private static boolean isEndOfTerm(Parser parser, char f)
   {
      return (f == parser.EndOfTerm);
   }

   private static boolean isEndOfLine(char f)
   {
      return (f == '\n' || f == '\r');
   }

   /** a general pass over the list of tokens, we create terms and then pass over them creating combined terms */
   public static TokenList GroupBlockTokens(Parser parser, StringIterator i) 
   {
      return GroupTokens(parser, i, ';');
   }

   /** a general pass over the list of tokens, we create terms and then pass over them creating combined terms */
   public static TokenList GroupExpressionIndexTokens(Parser parser, StringIterator i) 
   {
      return GroupTokens(parser, i, ':');
   }

   private static TokenList GroupTokens(Parser parser, StringIterator i, char Eot)
   {
      parser.setEndOfTerm(Eot);

      Token[] terms = CreateTerms(parser, i).getTokens();

      TokenList value  = new TokenList();

      StringBuffer rhs = new StringBuffer();
      int          tok = 0;
 
      for (int x = 0; x < terms.length; x++)
      {
         if ((x + 1) < terms.length)
         {
            String a, b;
            a = terms[x].toString();
            b = terms[x + 1].toString();

            tok = x;

            if ((x + 2) < terms.length && Checkers.isClassLiteral(a) && b.equals("."))
            {
               rhs.append(terms[x]);

               /** collapse a literal class string plz */
               while ((x + 2) < terms.length && terms[x+1].toString().equals(".") && Checkers.isClassPiece(terms[x+2].toString()))
               {
                  rhs.append(".");
                  rhs.append(terms[x+2]);
                  x += 2;
               }
            }
            else if (Checkers.isFunctionCall(a, b) || Checkers.isIndexableItem(a, b))
            {
               rhs.append(a.toString());
               rhs.append(b.toString());

               x++;

               while ((x + 1) < terms.length && Checkers.isIndex(terms[x+1].toString()))
               {
                  rhs.append(terms[x+1].toString());
                  x++;
               }
            }
         }

         if (rhs.length() > 0)
         {
            value.add(ParserUtilities.makeToken(rhs.toString(), terms[tok]));
            rhs = new StringBuffer();
         }
         else if (! Checkers.isComment(terms[x].toString()))
         {
            value.add(terms[x]);
         }
         else
         {
            parser.addComment(terms[x].toString());
         }
      }

      return value;
   }

   public static TokenList GroupParameterTokens(Parser parser, StringIterator f)
   {
      return GroupTokens(parser, f, ',');
   }

   public static TokenList CreateTerms (Parser parser, StringIterator f)
   {
      return (CreateTerms(parser, f, true, true));
   } 

   public static TokenList CreateTerms (Parser parser, StringIterator f, boolean showEOT, boolean showEOL)
   {
      Rule temp[] = new Rule[7];
      temp[0] = PAREN_RULE.copyRule();  // Rule objects hold state information related to open and closed
      temp[1] = BLOCK_RULE.copyRule();  // constructs.  As such the static rules should never be passed
      temp[2] = DQUOTE_RULE.copyRule(); // directly to the lexer or the thread safeness of the lexer will
      temp[3] = SQUOTE_RULE.copyRule(); // be destroyed.  We definetly do not want that... :P
      temp[4] = INDEX_RULE.copyRule();
      temp[5] = BACKTICK_RULE.copyRule();
      temp[6] = COMMENT_RULE; // not really a necessity since comment rules just return themselves.

      return (CreateTerms(parser, f, temp, showEOT, showEOL));      
   }

   //
   // the purpose of this function is to skip past the current stuff and find the match to the passed in term.
   // 
   private static String AdvanceTerms(Parser report, StringIterator iterator, Rule term, Rule[] rules)
   {
      term.witnessOpen(new Token(iterator.getEntireLine(), iterator.getLineNumber(), iterator.getLineMarker()));

      StringBuffer value = new StringBuffer();

      int initialLine = iterator.getLineNumber();

      while (iterator.hasNext())
      {
         char temp = iterator.next();

         if (temp == '\\' && term.getType() == Rule.PRESERVE_SINGLE && term != COMMENT_RULE)
         {
             if (! iterator.hasNext() && report != null)
             {
                report.reportError("Escape is end of string", new Token(value.toString(), iterator.getLineNumber(), iterator.getLineMarker()));
             }
             else 
             {
                value.append(temp);
                value.append(iterator.next());
             } 
         }        
         else if (term.isRight(temp) || term.isMatch(temp))
         {
            term.witnessClose(new Token(iterator.getEntireLine(), iterator.getLineNumber(), iterator.getLineMarker()));
            return value.toString();
         }
         else if (term.getType() != Rule.PRESERVE_SINGLE && term != COMMENT_RULE)
         {
            boolean match = false;

            for (int x = 0; x < rules.length; x++)
            {
               if (rules[x].isLeft(temp) || rules[x].isMatch(temp))
               {
                   String result = AdvanceTerms(report, iterator, rules[x], rules);
                   
                   if (result != null)
                   {
                      value.append(rules[x].wrap(result));
                   }
                   else
                   {
                      return null;
                   }

                   match = true;
                   break;
               }
               else if (rules[x].isRight(temp) && rules[x] != term)
               {
                   rules[x].witnessClose(new Token(iterator.getEntireLine(), iterator.getLineNumber(), iterator.getLineMarker()));
               }
            }

            if (!match)
               value.append(temp);
         }
         else
         {
            value.append(temp);
         }
      }

      return null;
   }

   public static TokenList CreateTerms (Parser parser, StringIterator iterator, Rule rules[], boolean showEOT, boolean showEOL)
   {
      TokenList   terms = new TokenList();
      boolean     match = false;

      Token newTerm;

      StringBuffer aTerm = new StringBuffer();

      while (iterator.hasNext())
      {
         match = false;

         char temp = iterator.next();

         for (int x = 0; x < rules.length; x++)
         {
            if (rules[x].isLeft(temp) || rules[x].isMatch(temp))
            {
               if (aTerm.length() > 0)
               { 
                  newTerm = new Token(trim(parser, aTerm.toString()), iterator.getLineNumber());
                  terms.add(newTerm);
                  aTerm = new StringBuffer();
               }
 
               int curLine = iterator.getLineNumber(); // lets get the current line not the line at the "end" of the term

               String result = AdvanceTerms(parser, iterator, rules[x], rules);

               if (result == null)
                  result = "";  // let it wrap something

               newTerm = new Token(rules[x].wrap(result), curLine);
               terms.add(newTerm);

               match = true;
               break;
            }
            else if (rules[x].isRight(temp))
            {
               rules[x].witnessClose(new Token(iterator.getEntireLine(), iterator.getLineNumber()));
            }
         }

         if (match)
             continue;

         if (isEndOfTerm(parser, temp))
         {
             if (aTerm.length() > 0)
             {
                newTerm = new Token(trim(parser, aTerm.toString()), iterator.getLineNumber());
                terms.add(newTerm);
                aTerm = new StringBuffer();
             }

             newTerm = new Token("EOT", iterator.getLineNumber());
             terms.add(newTerm);
         }
         else if (isBuiltInOperator(temp, aTerm, iterator))
         {
             if (aTerm.length() > 0)
             {
                newTerm = new Token(trim(parser, aTerm.toString()), iterator.getLineNumber());
                terms.add(newTerm);
                aTerm = new StringBuffer();
             }

             terms.add(new Token(temp+"", iterator.getLineNumber())); // add the built in operator as a token...
         }
         else if (isSkippable(parser, temp))
         {
             if (aTerm.length() > 0)
             {
                /* why is this happening here, you may be asking.  Well for the sake of future generations
                   I introduced a nasty ambiguity creating a function %() for intializing hashes which of
                   course to my friendly top down parser is easily mistaken with 3 % (some expr) for doing
                   typical modulus operations.  So if there is whitespace following the % char then I preserve
                   it so the parser can differentiate a hash literal from a MOD math operation. */
                if (aTerm.length() == 1 && aTerm.charAt(0) == '%')
                {
                   newTerm = new Token("% ", iterator.getLineNumber());
                }
                else
                {
                   newTerm = new Token(trim(parser, aTerm.toString()), iterator.getLineNumber());
                }
                terms.add(newTerm);
                aTerm = new StringBuffer();
             }
         }
         else
         {
            aTerm.append(temp);
         }
      } 

      if (aTerm.length() > 0)
      {
         newTerm = new Token(trim(parser, aTerm.toString()), iterator.getLineNumber());
         terms.add(newTerm);
      }

      for (int x = 0; x < rules.length; x++)
      {
          if (!rules[x].isBalanced())
            parser.reportError(rules[x].getSyntaxError());
      }

      return terms;
   }

   public static String trim(Parser parser, String blah)
   {
      if (blah.length() == 0 || blah.equals(" "))
      {
         return "";
      }

      int x = 0;
      while (x < blah.length() && isSkippable(parser, blah.charAt(x)))
      {
         x++;
      }

      int y = blah.length() - 1;
      while (y > 0 && isSkippable(parser, blah.charAt(y)))
      {
         y--;
      }

      if (x > y)
      {
         return "";
      }

      return blah.substring(x, y+1);
   }
}

