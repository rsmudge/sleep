/* 
   SLEEP - Simple Language for Environment Extension Purposes 
 .----------------------------.
 | sleep.parser.CodeGenerator |_______________________________________________
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
    11.15.2002
     - moved a number of static functions to the ParserUtilities class
     - factored out token parser functions into TokenParser class
     - cleaned up lots of code
     - factored out Token Class
     - did some code cleanup
    
     9.08.2002
     - rewrote and refactored the Parser, dividing the responsibilities 
       between a LexicalAnalyzer and a series of Checkers.  What a nightmare.

     4.20.2002
     - added better debugging support.

     4.15.2002
     - added support for parsing native floats.

   * This software is distributed under the artistic license, see license.txt
     for more information. *
   
 |____________________________________________________________________________|
 */

package sleep.parser;

import java.util.*;
import java.io.*;

import sleep.engine.*;
import sleep.engine.atoms.*;
import sleep.error.*;
import sleep.runtime.*;

/** Generates code for the parser.  The main thing here developers might concern themselves with is the installEscapeConstant 
function */
public class CodeGenerator implements ParserConstants
{
   protected Block CURRENT_BLOCK;
   protected Stack BACKUP_BLOCKS;

   protected GeneratedSteps factory; /* allow specification of a factory for generating steps */
   protected Parser         parser;

   protected static HashMap escape_constants = new HashMap();

   static
   {
      installEscapeConstant('t', "\t");
      installEscapeConstant('n', "\n");
      installEscapeConstant('r', "\r");
   }

   /** install a constant for escapes within strings i.e. \n could be "\n" */
   public static void installEscapeConstant(char c, String value)
   {
      escape_constants.put(c+"", value);
   }

   public Block getRunnableBlock()
   {
      return CURRENT_BLOCK;
   }

   public void add(Step n, Token token)
   {
      CURRENT_BLOCK.add(n);
      n.setInfo(token.getHint());
   }

   public void backup()
   {
      BACKUP_BLOCKS.push(CURRENT_BLOCK);
      CURRENT_BLOCK = new Block(parser.getName());
   }

   public Block restore()
   {
      Block temp = CURRENT_BLOCK;
      CURRENT_BLOCK = (Block)(BACKUP_BLOCKS.pop());
      return temp;
   }

   public CodeGenerator(Parser _parser, GeneratedSteps _factory)
   {
      parser = _parser;
      factory = _factory != null ? _factory : new GeneratedSteps();

      CURRENT_BLOCK = new Block(parser.getName());
      BACKUP_BLOCKS = new Stack();
   }

   public CodeGenerator(Parser _parser)
   {
       this(_parser, null);
   }

   public Check parsePredicate(Token data)
   {
       /** send the data string through the parser pipeline - errors earlier in the pipeline are assumed to not exist as they would have been found the first time we processed it as a block */
       Statement allData = TokenParser.ParsePredicate(parser, LexicalAnalyzer.GroupBlockTokens(parser, new StringIterator(data.toString(), data.getHint())));

       return parsePredicate(allData);
   }

   public Check parsePredicate(Statement parsePred)
   {
       Token[]  tokens  = parsePred.getTokens();
       String[] strings = parsePred.getStrings();

       Step atom;
       Check tempc;
       Block backup, a, b;
       Stack queue;  // locals is a stack created at parse time but used by the operators for keeping track of stuff.. yeah

       switch (parsePred.getType())
       {
         case PRED_EXPR:
           return parsePredicate(ParserUtilities.extract(tokens[0]));
         case PRED_IDEA: // comparing the idea != 0 to say its true... -istrue predicate provided in BasicUtilities
           if (strings[0].charAt(0) == '!' && strings[0].length() > 1)
           {
              return parsePredicate(tokens[0].copy("!-istrue (" + strings[0].substring(1, strings[0].length()) + ")"));
           }
           else
           {
              return parsePredicate(tokens[0].copy("-istrue (" + strings[0] + ")"));
           }
         case PRED_BI:
           // <idea> <string> <idea>
           backup();

           parseIdea(tokens[0]);
           parseIdea(tokens[2]);

           tempc = factory.Check(strings[1], restore()); // a KillFrame is implied here
           tempc.setInfo(tokens[1].getHint());

           return tempc;
         case PRED_UNI:
           backup();

           parseIdea(tokens[1]);
             
           tempc = factory.Check(strings[0], restore());
           tempc.setInfo(tokens[0].getHint());

           return tempc;
         case PRED_AND:
           tempc = null;
           queue = new Stack();
           
           for (int x = 0; x < tokens.length; x++)
           {
              queue.push(tokens[x]);
           }           
           while (!queue.isEmpty())
           { 
              Token t = (Token)(queue.pop());
              if (!t.toString().equals("&&"))
              {
                 Check oldtemp = tempc;
                 tempc = parsePredicate(t);
                 tempc.setChoices(oldtemp, null);
              }
           }
           return tempc;
         case PRED_OR:
           tempc = null;
           queue = new Stack();

           for (int x = 0; x < tokens.length; x++)
           {
              queue.push(tokens[x]);
           }          

           while (!queue.isEmpty())
           { 
              Token t = (Token)(queue.pop());
              if (!t.toString().equals("||"))
              {
                 Check oldtemp = tempc;
                 tempc = parsePredicate(t);
                 tempc.setChoices(null, oldtemp);
              }
           }

           return tempc;
      }

      parser.reportError("Unknown predicate.", tokens[0].copy(parsePred.toString()));
      return null;   
   }

   public void parseObject(Token data)
   {
      Statement stmt = TokenParser.ParseObject(parser, LexicalAnalyzer.GroupExpressionIndexTokens(parser, new StringIterator(data.toString(), data.getHint())));

      if (parser.hasErrors())
      {
         return;
      }

/*      System.out.println(stmt);

      for (int x = 0; x < stmt.getStrings().length; x++)
      {
          System.out.println(">>> " + stmt.getStrings()[x]);
      } */

      parseObject(stmt);
   }

   public void parseObject(Statement datum)
   {
       Step     atom;

       String[] strings = datum.getStrings(); // was "temp"
       Token[]  tokens  = datum.getTokens();

       Class aClass = null;

       switch (datum.getType())
       {
         case OBJECT_NEW:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.length > 1)
           {
              parseParameters(tokens[1]);
           }

           aClass = parser.findImportedClass(strings[0]);

           if (aClass == null)
              parser.reportError("Class " + strings[0] + " was not found", tokens[0]);

           atom    = factory.ObjectNew(aClass);
           add(atom, tokens[0]);
           break;
        case OBJECT_CL_CALL: 
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.length > 1)
           {
              parseParameters(tokens[1]);
           }

           parseIdea(tokens[0]);

           atom    = factory.ObjectAccess(null);
           add(atom, tokens[0]);
           break;
        case OBJECT_ACCESS:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.length > 2)
           {
              parseParameters(tokens[2]);
           }

           parseIdea(tokens[0]);

           atom    = factory.ObjectAccess(strings[1]);
           add(atom, tokens[0]);
           break;
         case OBJECT_ACCESS_S:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.length > 2)
           {
              parseParameters(tokens[2]);
           }

           aClass = parser.findImportedClass(strings[0]);

           if (aClass == null)
              parser.reportError("Class " + strings[0] + " was not found", tokens[0]);
           
           atom = factory.ObjectAccessStatic(aClass, strings[1]);
           add(atom, tokens[0]);
           break;
       }
   }
   
   public void parseBlock(Token data)
   {
      /** send the data string through the parser pipeline - errors earlier in the pipeline are assumed to not exist as they would have been found the first time we processed it as a block */
      LinkedList allData = TokenParser.ParseBlocks(parser, LexicalAnalyzer.GroupBlockTokens(parser, new StringIterator(data.toString(), data.getHint())));

      if (parser.hasErrors())
      {
         return;
      }

      if (allData.size() == 0)
      {
         Step temp = new Step();
         add(temp, data);
      }
      else
      {
         parseBlock(allData);
      }
   }

   public void parseBlock(LinkedList data)
   {  
      Iterator i = data.iterator();
      while (i.hasNext())
      {
         parse((Statement)i.next());
      }
   }
  
   public List parseIdea(Token data) 
   {
      LinkedList allData = TokenParser.ParseIdea(parser, LexicalAnalyzer.GroupBlockTokens(parser, new StringIterator(data.toString(), data.getHint())));
      
      if (parser.hasErrors())
      {
         return null;
      }

      Iterator i = allData.iterator();
      while (i.hasNext())
      {
         parse((Statement)i.next());
      }

      return allData;
   }

   public void parse(Statement datum)
   {
       Block    a, b;
       String[] scratch;
       Step     atom;
       Scalar   ascalar;

       Check    tempp;

       Iterator i;
       List     ll;
       String   mutilate; // mutilate this string as I see fit...
       StringBuffer sb;  

       String[] strings = datum.getStrings(); // was "temp"
       Token[]  tokens  = datum.getTokens();

       switch (datum.getType())
       {
         case VALUE_SCALAR_REFERENCE:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           atom = factory.Get(strings[0].substring(1)); /* generate rhs with scalar value */
           add(atom, tokens[0]);

           ascalar = SleepUtils.getScalar(strings[0].substring(1)); /* generate lhs string scalar with var name */
           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);

           atom = factory.Operate("=>");
           add(atom, tokens[0]);
           break;
         case IDEA_HASH_PAIR:
           //
           // parsing A => B
           //
           atom = factory.CreateFrame();
           add(atom, tokens[2]);

           //
           // parse B
           //
           List valuez = parseIdea(tokens[2]);

           /* a bit of error checking to guard against a common error */
           Iterator iz = valuez.iterator();
           while (iz.hasNext())
           {
              Statement t = (Statement)iz.next();
              if (t.getType() == IDEA_HASH_PAIR)
              {
                 parser.reportError("key/value pair specified for '"+tokens[0]+"', did you forget a comma?", tokens[2]);
              }
           }

           //
           // parse A - or just push it onto the stack as a literal token :)
           //
           ascalar = SleepUtils.getScalar(strings[0]);
           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);

           //
           // parse operator
           //
           atom = factory.Operate(strings[1]);
           add(atom, tokens[1]);
           break;
         case IDEA_OPER:
           //
           // parsing A operator B
           //
           atom = factory.CreateFrame();
           add(atom, tokens[2]);

           //
           // parse B
           //
           parseIdea(tokens[2]);

           //
           // parse A
           //
           parseIdea(tokens[0]);

           //
           // parse operator
           //
           atom = factory.Operate(strings[1]);
           add(atom, tokens[1]);
           break;
         case IDEA_EXPR_I:
           parseObject(ParserUtilities.extract(tokens[0]));
           break;
         case IDEA_LITERAL: // implemented                   
           sb = new StringBuffer(ParserUtilities.extract(strings[0]));

           for (int x = 0; x < sb.length(); x++)
           {
              if (sb.charAt(x) == '\\' && (x + 1) < sb.length())
              {
                 char tempc = sb.charAt(x + 1);

                 if (tempc == '\'' || tempc == '\\')
                 {
                    sb.deleteCharAt(x);
                 }
              }
           }

           ascalar = SleepUtils.getScalar(sb.toString());
           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);
           break;
         case IDEA_NUMBER:                         // implemented
           if (strings[0].endsWith("L"))
           {
              ascalar = SleepUtils.getScalar(Long.decode(strings[0].substring(0, strings[0].length() - 1)).longValue());
           }
           else
           {
              ascalar = SleepUtils.getScalar(Integer.decode(strings[0]).intValue());
           }

           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);
           break;
         case IDEA_DOUBLE:                         // implemented
           ascalar = SleepUtils.getScalar(Double.parseDouble(strings[0]));
           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);
           break;
         case IDEA_BOOLEAN:                         // implemented
           ascalar = SleepUtils.getScalar(Boolean.valueOf(strings[0]).booleanValue());
           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);
           break;
         case IDEA_CLASS:
           Class claz = parser.findImportedClass(strings[0].substring(1));
 
           if (claz == null)
           {
              parser.reportError("unable to resolve class: " + strings[0].substring(1), tokens[0]);
           } 
           else
           {          
              ascalar = SleepUtils.getScalar(parser.findImportedClass(strings[0].substring(1)));
              atom    = factory.SValue(ascalar);
              add(atom, tokens[0]);
           }
           break;
         case VALUE_SCALAR:                       //   implemented
           if (strings[0].equals("$null"))
           {
              ascalar = SleepUtils.getEmptyScalar();
              atom    = factory.SValue(ascalar);
              add(atom, tokens[0]);
           }
           else
           {
              atom = factory.Get(strings[0]);
              add(atom, tokens[0]);
           }
           break;
         case VALUE_INDEXED:
           parseIdea(tokens[0]); // parse the thing we're going to index stuff off of..

           for (int z = 1; z < tokens.length; z++)
           {         
              backup();

              atom = factory.CreateFrame();
              add(atom, tokens[0]);
              parseIdea(ParserUtilities.extract(tokens[z]));

              atom = factory.Index(strings[0], restore());
              add(atom, tokens[0]);
           }
           break;
         case IDEA_EXPR:                         // implemented
           parseIdea(ParserUtilities.extract(tokens[0]));
           break;
         case EXPR_EVAL_STRING:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           datum.setType(IDEA_STRING);
           parse(datum);

           atom = factory.Call("__EXEC__");
           add(atom, tokens[0]);
           break;
         case IDEA_STRING: // implemented -- parsed literals, one of my favorite features in sleep

           /** create a frame, we assume the PLiteral machine will destroy it */
           atom = factory.CreateFrame();
           add(atom, tokens[0]);
           
           boolean isVar = false; // is the current buffer d a varname or not?

           String varname, align; // some temp vars we'll use later...
           
           StringBuffer d = new StringBuffer(); // the string buffer where we will dump our results.

           ll = new LinkedList();
           StringIterator si = new StringIterator(ParserUtilities.extract(strings[0]), tokens[0].getHint());
   
           while (si.hasNext())
           {
              char current = si.next();

              if (current == '\\' && si.hasNext())
              {
                 current  = si.next();
                 mutilate = current + ""; 
                
                 if (escape_constants.containsKey(mutilate))
                 {
                     d.append(escape_constants.get(mutilate));
                 }
                 else if (current == 'u')
                 {
                     if (!si.hasNext(4))    
                     {
                        parser.reportErrorWithMarker("not enough remaining characters for \\uXXXX", si.getErrorToken());
                     }
                     else
                     {
                        mutilate = si.next(4);

                        try
                        {
                           int codepoint = Integer.parseInt(mutilate, 16);
                           d.append((char)codepoint);
                        }
                        catch (NumberFormatException nex)
                        {
                           parser.reportErrorWithMarker("invalid unicode escape \\u"+mutilate+" - must be hex digits", si.getErrorToken());
                        }
                     }
                  }
                  else if (current == 'x')
                  {
                     if (!si.hasNext(2))
                     {
                        parser.reportErrorWithMarker("not enough remaining characters for \\xXX", si.getErrorToken());
                     }
                     else
                     {
                        mutilate = si.next(2);

                        try
                        {
                           int codepoint = Integer.parseInt(mutilate, 16);
                           d.append((char)codepoint);
                        }
                        catch (NumberFormatException nex)
                        {
                           parser.reportErrorWithMarker("invalid unicode escape \\x"+mutilate+" - must be hex digits", si.getErrorToken());
                        }
                     }
                  }
                  else
                  {
                     // default behavior is to append the current character ignoring the previous escape.
                     d.append(current);  
                  }
              }
              else if (current == ' ' && si.isNextString("$+ "))
              {
                  si.skip(3);
              }
              else if (current == '$' && si.isNextChar('+'))
              {
                  parser.reportErrorWithMarker("operator $+ must be surrounded with whitespace", si.getErrorToken());
              }
              else if (isVar && (Checkers.isEndOfVar(si.peek()) || !si.hasNext()))
              {
                  d.append(current);

                  String[] ops = LexicalAnalyzer.CreateTerms(parser, new StringIterator(d.toString(), si.getLineNumber())).getStrings();

                  if (ops.length == 3)
                  {
                     // ^--- check if our varref has the form $[whatever]varname
                     // in which case we are taking advantage of the align operator inside
                     // parsed literal strings.

                     varname = ops[0] + ops[2];
                     align   = ParserUtilities.extract(ops[1]);

                     if (align.length() > 0)
                     {
                        parseIdea(new Token(align, si.getLineNumber()));
                        ll.add(PLiteral.fragment(PLiteral.ALIGN_FRAGMENT, null));
                     }
                     else
                     {
                        parser.reportErrorWithMarker("Empty alignment specification for " + varname, si.getErrorToken());
                     }
                  }
                  else
                  {
                     varname = d.toString();
                  }

                  parseIdea(new Token(varname, si.getLineNumber()));
                  ll.add(PLiteral.fragment(PLiteral.VAR_FRAGMENT, null));

                  isVar   = false;
                  d       = new StringBuffer();
              }
              else if (current == '$' && !Checkers.isEndOfVar(si.peek()) && si.hasNext())
              {
                  ll.add(PLiteral.fragment(PLiteral.STRING_FRAGMENT, d.toString()));
                  d = new StringBuffer();
                  d.append('$');

                  isVar = true;

                  if (si.isNextChar('['))
                  {
                     int count = 0;
                     do
                     {
                        current = si.next();
                        if (current == '[')
                            count++;

                        if (current == ']')
                            count--;

                        d.append(current);
                     } while (si.hasNext() && count > 0);

                     if (count != 0)
                     {
                        parser.reportError("missing close brace for variable alignment", new Token(d.toString(), si.getLineNumber()));
                        isVar = false;
                     }
                     else if (!si.hasNext() || Checkers.isEndOfVar(si.peek()))
                     {
                        parser.reportErrorWithMarker("can not align an empty variable", si.getErrorToken());
                        isVar = false;
                     }
                  }
              }
              else
              {
                  d.append(current);
              }

              if (!si.hasNext() && d.length() > 0)
                 ll.add(PLiteral.fragment(PLiteral.STRING_FRAGMENT, d.toString()));
           }

           atom = factory.PLiteral(ll);
           add(atom, tokens[0]);
           break;
         case HACK_INC: // implemented
           mutilate = strings[0].substring(0, strings[0].length() - 2);
           parseBlock(new Token(mutilate + " = " + mutilate + " + 1;", tokens[0].getHint()));
           break;
         case HACK_DEC: // implemented
           //
           // [TRANSFORM]: Reconstructing "+temp[0]+" deccrement hack
           //
           mutilate = strings[0].substring(0, strings[0].length() - 2);
           parseBlock(new Token(mutilate + " = " + mutilate + " - 1;", tokens[0].getHint()));
           break;
         case EXPR_BIND_PRED:
           //
           // [BIND PREDICATE FUNCTION]: "+temp[0]+" "+temp[1]);
           //
           backup();
           parseBlock(tokens[2]);
           atom = factory.BindPredicate(strings[0], parsePredicate(ParserUtilities.extract(tokens[1])), restore());
           add(atom, tokens[0]);
           break; 
         case EXPR_BIND_FILTER:
           //
           // [BIND PREDICATE FUNCTION]: on | EVENT | expression | { code }
           //

           backup();
           parseBlock(tokens[3]);
           b = restore();

           atom = factory.BindFilter(strings[0], strings[1], b, strings[2]);
           add(atom, tokens[0]);
           break; 
         case EXPR_BIND: // implemented
           //
           // [BIND FUNCTION]: "+temp[0]+" "+temp[1]);
           //
           backup();

           if (Checkers.isString(strings[1]) || Checkers.isLiteral(strings[1]))
           {
              parseIdea(tokens[1]);
           }
           else
           {
              parseIdea(new Token("'"+strings[1]+"'", tokens[1].getHint()));
           }

           Block nameBlock = restore();
 
           backup();
           parseBlock(tokens[2]);
           atom = factory.Bind(strings[0], nameBlock, restore());
           add(atom, tokens[0]);
           break; 
         case EXPR_TRYCATCH: 
           //
           // [TRYCATCH]: try | BLOCK | catch | VAR | BLOCK
           // 

           // now parse the code we want to try as if nothing special ever occured.
           backup();

              /* do the normal version of this, the code we want to "try" */
           parseBlock(ParserUtilities.extract(tokens[1]));

              /* pop the handler */
           atom = factory.PopTry();
           add(atom, tokens[4]);

           a = restore();

           // setup the thrown value handler and the stuff to install it...
           backup();

              /* pop the handler [ensure this happens first thing in our handler block] */
           atom = factory.PopTry();
           add(atom, tokens[4]);

              /* parse the handler */
           parseBlock(ParserUtilities.extract(tokens[4]));

           b = restore();

           // add this try/catch bits to the current block
           atom = factory.Try(a, b, strings[3]);
           add(atom, tokens[0]);
           break;
         case EXPR_BLOCK:  // implemented
           parseBlock(ParserUtilities.extract(tokens[0]));
           break;
         case IDEA_BLOCK:  // turns our block into a scalar :)
           backup();

           parseBlock(ParserUtilities.extract(tokens[0]));

           atom    = factory.CreateClosure(restore());
           add(atom, tokens[0]);
           break;
         case IDEA_FUNC: // implemented 
           TokenList funcParms = LexicalAnalyzer.CreateTerms(parser, new StringIterator(strings[0], tokens[0].getHint()));

           strings = funcParms.getStrings(); 
           tokens  = funcParms.getTokens();

           if (strings[0].charAt(0) != '&')
           {
              strings[0] = '&' + strings[0];
           }

           if ((strings[0].equals("&iff") || strings[0].equals("&?")) && tokens.length > 1)
           {
              TokenList terms = ParserUtilities.groupByParameterTerm(parser, ParserUtilities.extract(tokens[1]));
              Token[] termsAr = terms.getTokens();

              backup();
              if (termsAr.length >= 2)
              {
                 parseIdea(termsAr[1]);
              }
              else
              {
                 parseIdea(termsAr[0].copy("true"));
              }
              a = restore();

              backup();
              if (termsAr.length == 3)
              {
                 parseIdea(termsAr[2]);
              }
              else
              {
                 parseIdea(termsAr[0].copy("false"));
              }
              b = restore();

              atom = factory.Decide(parsePredicate(termsAr[0]), a, b);
              add(atom, tokens[0]); 
           }
           else if (tokens.length > 1)
           {
              atom = factory.CreateFrame();
              add(atom, tokens[0]);

              /* if we're dealing with the warn function, sneak the current line number in as an argument. */
              if (strings[0].equals("&warn"))
              {
                 atom    = factory.SValue(SleepUtils.getScalar(tokens[0].getHint()));
                 add(atom, tokens[0]);
              }

              parseParameters(ParserUtilities.extract(tokens[1]));

              atom = factory.Call(strings[0]);
              add(atom, tokens[0]);
           }
           else
           {
              // transform a function literal into call on &function('&literal') 
              atom = factory.CreateFrame();
              add(atom, tokens[0]);

              atom = factory.SValue(SleepUtils.getScalar(strings[0]));
              add(atom, tokens[0]);

              atom = factory.Call("function");
              add(atom, tokens[0]);
           }
           break;
         case EXPR_WHILE:                                        // done
           backup();
           parseBlock(tokens[2]);    
           atom = factory.Goto(parsePredicate(ParserUtilities.extract(tokens[1])), restore(), null);
           add(atom, tokens[1]);
           break;
         case EXPR_WHILE_SPECIAL:                                        // done
           /* 0 = while
              1 = $var
              2 = (expression) 
              3 = {block} */

           // handle the actual block of code
           backup();
           parseBlock(tokens[3]);    
           b = restore(); 

           // handle the assignment step please (Assign will push the RHS onto the stack)
           backup();

           // assign:
           // 1 = $var
           // 2 = (expression) to assign, you know?!?

           atom = factory.CreateFrame();
           add(atom, tokens[2]);

           parseIdea(tokens[2]);

           backup();
           parseIdea(tokens[1]);

           atom = factory.Assign(restore());
           add(atom, tokens[2]);

           // end assign...
           
           // push $null onto the current frame as well...
           add(factory.SValue(SleepUtils.getEmptyScalar()), tokens[2]); // for comparisons sake

           a = restore();

           // dew the lewp while the assigned value is not $null
           tempp = factory.Check("!is", a);
           tempp.setInfo(tokens[1].getHint());

           atom = factory.Goto(tempp, b, null);
           add(atom, tokens[1]);

           break;
         case EXPR_ASSIGNMENT_T:                                  // implemented
         case EXPR_ASSIGNMENT_T_OP:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           TokenList terms2 = ParserUtilities.groupByParameterTerm(parser, ParserUtilities.extract(tokens[0]));
           Token[] termsAr2 = terms2.getTokens();

           for (int x = 0; x < termsAr2.length; x++)
           {
              parseIdea(termsAr2[x]);
           }

           parseIdea(tokens[2]);

           if (datum.getType() == EXPR_ASSIGNMENT_T_OP)
           {
              atom = factory.AssignTupleAndOperate(strings[1].substring(0, strings[1].length() - 1));
           }
           else
           {
              atom = factory.AssignT();
           }
           add(atom, tokens[0]);
           break;
         case EXPR_ASSIGNMENT:                                  // implemented
         case EXPR_ASSIGNMENT_OP:                                  // implemented
           atom = factory.CreateFrame();
           add(atom, tokens[2]);

           parseIdea(tokens[2]);

           backup();
           parseIdea(tokens[0]);

           if (datum.getType() == EXPR_ASSIGNMENT_OP)
           {
              atom = factory.AssignAndOperate(restore(), strings[1].substring(0, strings[1].length() - 1));
           }
           else
           {
              atom = factory.Assign(restore());
           }
           add(atom, tokens[2]);
           break;
         case EXPR_IF_ELSE:                                // done
           //
           // if <cond> <block> else -do this again-
           //
           // parse an if-else statement.

           backup();
           parseBlock(tokens[2]);
           a = restore();

           backup();
           if (tokens.length >= 4)
           {
              if (strings[4].equals("if"))
              {
                 parseBlock(ParserUtilities.join(ParserUtilities.get(tokens, 4, tokens.length)));
              }
              else
              {
                 parseBlock(tokens[4]); // get the rest of the arguments after the else clause...  this way we can do really long and big nested if-else statements.
              }
           }
           b = restore();

           atom = factory.Decide(parsePredicate(ParserUtilities.extract(tokens[1])), a, b);
           add(atom, tokens[1]); 
           break;
         case EXPR_FOREACH_SPECIAL:
           // |foreach   0
           // |$key      1
           // |=>        2
           // |$value    3
           // |(@temp)   4
           // |{ &printf("hi"); } 5
 
           /**** purposeful fall thru... ****/

         case EXPR_FOREACH:
           // |foreach
           // |$var
           // |(@temp)
           // |{ &printf("hi"); }

           //
           // setup our frame with the value, possibly the key,  and the source
           //
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (datum.getType() == EXPR_FOREACH)
           {
              parseIdea(ParserUtilities.extract(tokens[2])); // parse the "source" of the foreach
              atom = factory.IteratorCreate(null, strings[1]);
           }
           else
           {
              parseIdea(ParserUtilities.extract(tokens[4])); // parse the "source" of the foreach
              atom = factory.IteratorCreate(strings[1], strings[3]);
           }
           add(atom, tokens[0]);

           //
           // parse the body of the loop
           // 
           backup();

           if (datum.getType() == EXPR_FOREACH)
           {
              parseBlock(ParserUtilities.extract(tokens[3])); // parse the actual block of code to be executed.
           }
           else
           {
              parseBlock(ParserUtilities.extract(tokens[5])); // parse the actual block of code to be executed.
           }

           a = restore();

           //
           // setup the has next portion of the iterator...
           //
           backup();

           atom = factory.IteratorNext();
           add(atom, tokens[0]);
             
           tempp = factory.Check("-istrue", restore());
           tempp.setInfo(tokens[0].getHint());

           //
           // add our looping mechanism (everyone loves this part, eh!?!)
           //
           atom = factory.Goto(tempp, a, null);
           add(atom, tokens[1]);

           //
           // add our mechanism for destroying the iterator...
           //
           atom = factory.IteratorDestroy();
           add(atom, tokens[1]);
           break; 
         case EXPR_FOR:
           // |for
           // |($x = 0; $x < 100; $x++)
           // |{ &printf("hi"); }

           Token extracted_terms[] = ParserUtilities.groupByBlockTerm(parser, ParserUtilities.extract(tokens[1])).getTokens();

           //
           // evaluate initial terms...           
           //
           StringBuffer doThis = new StringBuffer();

           TokenList initial_terms = ParserUtilities.groupByParameterTerm(parser, extracted_terms[0]);

           i = initial_terms.getList().iterator();
           while (i.hasNext())
           {
              doThis.append(i.next().toString());
              doThis.append("; ");
           }

           parseBlock(tokens[0].copy(doThis.toString()));

           //
           // parse the final terms and save them...
           //

           if (extracted_terms.length == 3)
           {
              backup();
              doThis = new StringBuffer();

              TokenList final_terms = ParserUtilities.groupByParameterTerm(parser, extracted_terms[2]);

              i = final_terms.getList().iterator();
              while (i.hasNext())
              {
                 doThis.append(i.next().toString());
                 doThis.append("; ");
              }

              parseBlock(tokens[0].copy(doThis.toString()));
              a = restore();
           }
           else
           {
              a = null;
              doThis = new StringBuffer();
           }

           //
           // parse the loop body and save it
           //
           backup();

           parseBlock(tokens[2]);    
           parseBlock(tokens[0].copy(doThis.toString()));
           b = restore();

           //
           // setup our goto object..
           // 
           atom = factory.Goto(parsePredicate(extracted_terms[1]), b, a);
           add(atom, tokens[1]);
           break;
         case OBJECT_IMPORT:
           try
           {
              if (strings.length == 1)
              {
                 parser.importPackage(strings[0], null);
              }
              else
              {
                 if (Checkers.isString(strings[1]) || Checkers.isLiteral(strings[1]))
                    strings[1] = ParserUtilities.extract(strings[1]);

                 parser.importPackage(strings[0], strings[1]);
              }
           }
           catch (Exception ex)
           {
              if (tokens.length == 2)
              {
                 parser.reportError(ex.getMessage(), ParserUtilities.makeToken("import " + strings[0] + " from: " + strings[1], tokens[1]));
              }
              else
              {
                 parser.reportError(ex.getMessage(), ParserUtilities.makeToken("import " + strings[0], tokens[0]));
              }
           }
           break;           
         case EXPR_ASSERT:
           if (tokens.length == 1)
           {
              parser.reportError("Assertion can't be empty!", tokens[0]);
              return;
           }

           if (Boolean.valueOf(System.getProperty("sleep.assert", "true")) == Boolean.FALSE)
           {
              return;
           }

           Token assert_terms[] = ParserUtilities.groupByMessageTerm(parser, tokens[1]).getTokens();
           
           backup();
              atom = factory.CreateFrame();
              add(atom, tokens[0]);

              if (assert_terms.length == 1)
              {
                 ascalar = SleepUtils.getScalar("assertion failed");
                 atom    = factory.SValue(ascalar);
                 add(atom, tokens[0]);
              }
              else
              {
                 parseIdea(assert_terms[1]);
              }

              atom = factory.Call("&exit");
              add(atom, tokens[0]);
           b = restore();

           atom = factory.Decide(parsePredicate(assert_terms[0]), null, b);
           add(atom, tokens[1]);
           break;
         case EXPR_RETURN:                     // implemented
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (strings[0].equals("done"))
           {
              parseIdea(tokens[0].copy("1"));  // in jIRC speak this means just plain old return
           }
           else if (strings[0].equals("halt"))
           {
              parseIdea(tokens[0].copy("2"));  // 2 in jIRC speak means halt the event processing...
           }
           else if (tokens.length >= 2)
           {
              parseIdea(tokens[1]);
           }
           else
           {
              parseIdea(tokens[0].copy("$null"));
           }

           if (strings[0].equals("break"))
           {
              atom = factory.Return(ScriptEnvironment.FLOW_CONTROL_BREAK);
              add(atom, tokens[0]);
           }
           else if (strings[0].equals("continue"))
           {
              atom = factory.Return(ScriptEnvironment.FLOW_CONTROL_CONTINUE);
              add(atom, tokens[0]);
           }
           else if (strings[0].equals("throw"))
           {
              atom = factory.Return(ScriptEnvironment.FLOW_CONTROL_THROW);
              add(atom, tokens[0]);
           }
           else if (strings[0].equals("yield"))
           {
              atom = factory.Return(ScriptEnvironment.FLOW_CONTROL_YIELD);
              add(atom, tokens[0]);

              /* for some reason, yield breaks in certain cases if a yield happens
                 at the end of a block and no other steps come after it.  this has
                 only reared its head once I started allowing recursive coroutines
                 to combat the problem I've opted to introduce a null operation
                 after each yield, this fixes the problem.  hopefully it doesn't
                 show itself in some other way in the future.  :~( *cry* */
//              atom = factory.NullOperation();
//              add(atom, tokens[0]);

              /* I've modified the evaluateOldContext method of ScriptEnvironment
                 to realize when the current script is being interrupted in the
                 middle of evaluating an old context, when this happens the 
                 unevaluated portions of the old context are saved along with the
                 current context.  I think this eliminates the need for the null
                 operation.  If coroutines break for some reason in the future feel
                 free to uncomment the two lines above (my original hacky fix for
                 the situation described in this comment */
           }
           else if (strings[0].equals("callcc"))
           {
              atom = factory.Return(ScriptEnvironment.FLOW_CONTROL_CALLCC);
              add(atom, tokens[0]);
           }
           else
           {
              atom = factory.Return(ScriptEnvironment.FLOW_CONTROL_RETURN);
              add(atom, tokens[0]);
           }
           break;
         default:
      }     
   }

   public void parseParameters(Token token)
   {
      TokenList terms   = ParserUtilities.groupByParameterTerm(parser, token);
      Token[]   termsAr = terms.getTokens();

      for (int x = termsAr.length - 1; x >= 0; x--)
      {
         parseIdea(termsAr[x]);
      }
   }
}

