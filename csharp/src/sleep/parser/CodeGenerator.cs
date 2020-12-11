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

using  sleep.engine;
using  sleep.engine.atoms;
using  sleep.error;
using  sleep.runtime;

namespace sleep.parser{


/** Generates code for the parser.  The main thing here developers might concern themselves with is the installEscapeConstant 
function */
public class CodeGenerator : ParserConstants
{
   protected Block CURRENT_BLOCK;
   protected java.util.Stack<Object> BACKUP_BLOCKS;

   protected GeneratedSteps factory; /* allow specification of a factory for generating steps */
   protected Parser         parser;

   protected static java.util.HashMap<Object,Object> escape_constants = new java.util.HashMap<Object,Object>();

   static CodeGenerator ()
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
      BACKUP_BLOCKS = new java.util.Stack<Object>();
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
       Check tempc, left, right;
       Block backup_, a, b;
       java.util.Stack<Object> queue;  // locals is a stack created at parse time but used by the operators for keeping track of stuff.. yeah

       switch (parsePred.getType())
       {
         case ParserConstants.PRED_EXPR:
           return parsePredicate(ParserUtilities.extract(tokens[0]));
         case PParserConstants.RED_IDEA: // comparing the idea != 0 to say its true... -istrue predicate provided in BasicUtilities
           if (strings[0].charAt(0) == '!' && strings[0].length() > 1)
           {
              return parsePredicate(tokens[0].copy("!-istrue (" + strings[0].substring(1, strings[0].length()) + ")"));
           }
           else
           {
              return parsePredicate(tokens[0].copy("-istrue (" + strings[0] + ")"));
           }
         case ParserConstants.PRED_BI:
           // <idea> <string> <idea>
           backup();

           parseIdea(tokens[0]);
           parseIdea(tokens[2]);

           tempc = factory.Check(strings[1], restore()); // a KillFrame is implied here
           tempc.setInfo(tokens[1].getHint());

           return tempc;
         case ParserConstants.PRED_UNI:
           backup();

           parseIdea(tokens[1]);
             
           tempc = factory.Check(strings[0], restore());
           tempc.setInfo(tokens[0].getHint());

           return tempc;
         case ParserConstants.PRED_AND:
           left = parsePredicate(tokens[0]);
           right = parsePredicate(tokens[1]);
           return factory.CheckAnd(left, right);
         case ParserConstants.PRED_OR:
           left = parsePredicate(tokens[0]);
           right = parsePredicate(tokens[1]);
           return factory.CheckOr(left, right);
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

       Type aClass = null;

       switch (datum.getType())
       {
         case ParserConstants.OBJECT_NEW:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.Length > 1)
           {
              parseParameters(tokens[1]);
           }

           aClass = parser.findImportedClass(strings[0]);

           if (aClass == null)
              parser.reportError("Class " + strings[0] + " was not found", tokens[0]);

           atom    = factory.ObjectNew(aClass);
           add(atom, tokens[0]);
           break;
        case ParserConstants.OBJECT_CL_CALL: 
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.Length > 1)
           {
              parseParameters(tokens[1]);
           }

           parseIdea(tokens[0]);

           atom    = factory.ObjectAccess(null);
           add(atom, tokens[0]);
           break;
        case ParserConstants.OBJECT_ACCESS:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.Length > 2)
           {
              parseParameters(tokens[2]);
           }

           parseIdea(tokens[0]);

           atom    = factory.ObjectAccess(strings[1]);
           add(atom, tokens[0]);
           break;
         case ParserConstants.OBJECT_ACCESS_S:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (tokens.Length > 2)
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
      java.util.LinkedList<Object> allData = TokenParser.ParseBlocks(parser, LexicalAnalyzer.GroupBlockTokens(parser, new StringIterator(data.toString(), data.getHint())));

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

   public void parseBlock(java.util.LinkedList<Object> data)
   {  
      java.util.Iterator<Object> i = data.iterator();
      while (i.hasNext())
      {
         parse((Statement)i.next());
      }
   }
  
   public java.util.List<Object> parseIdea(Token data) 
   {
      java.util.LinkedList<Object> allData = TokenParser.ParseIdea(parser, LexicalAnalyzer.GroupBlockTokens(parser, new StringIterator(data.toString(), data.getHint())));
      
      if (parser.hasErrors())
      {
         return null;
      }

      java.util.Iterator<Object> i = allData.iterator();
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

       java.util.Iterator<Object> i;
       java.util.List<Object>     ll;
       String   mutilate; // mutilate this string as I see fit...
       java.lang.StringBuffer sb;  

       String[] strings = datum.getStrings(); // was "temp"
       Token[]  tokens  = datum.getTokens();

       switch (datum.getType())
       {
         case ParserConstants.VALUE_SCALAR_REFERENCE:
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
         case ParserConstants.IDEA_HASH_PAIR:
           //
           // parsing A => B
           //
           atom = factory.CreateFrame();
           add(atom, tokens[2]);

           //
           // parse B
           //
           java.util.List<Object> valuez = parseIdea(tokens[2]);

           /* a bit of error checking to guard against a common error */
           java.util.Iterator<Object> iz = valuez.iterator();
           while (iz.hasNext())
           {
              Statement t = (Statement)iz.next();
              if (t.getType() == ParserConstants.IDEA_HASH_PAIR)
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
         case ParserConstants.IDEA_OPER:
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
         case ParserConstants.IDEA_EXPR_I:
           parseObject(ParserUtilities.extract(tokens[0]));
           break;
         case ParserConstants.IDEA_LITERAL: // implemented                   
           sb = new java.lang.StringBuffer(ParserUtilities.extract(strings[0]));

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
         case ParserConstants.IDEA_NUMBER:                         // implemented
           if (strings[0].endsWith("L"))
           {
              ascalar = SleepUtils.getScalar(java.lang.Long.decode(strings[0].substring(0, strings[0].length() - 1)).longValue());
           }
           else
           {
              ascalar = SleepUtils.getScalar(java.lang.Integer.decode(strings[0]).intValue());
           }

           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);
           break;
         case ParserConstants.IDEA_DOUBLE:                         // implemented
           ascalar = SleepUtils.getScalar(Double.Parse(strings[0]));
           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);
           break;
         case ParserConstants.IDEA_BOOLEAN:                         // implemented
           ascalar = SleepUtils.getScalar(Boolean.valueOf(strings[0]).booleanValue());
           atom    = factory.SValue(ascalar);
           add(atom, tokens[0]);
           break;
         case ParserConstants.IDEA_CLASS:
           Type claz = parser.findImportedClass(strings[0].substring(1));
 
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
         case ParserConstants.VALUE_SCALAR:                       //   implemented
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
         case ParserConstants.VALUE_INDEXED:
           parseIdea(tokens[0]); // parse the thing we're going to index stuff off of..

           for (int z = 1; z < tokens.Length; z++)
           {         
              backup();

              atom = factory.CreateFrame();
              add(atom, tokens[0]);
              parseIdea(ParserUtilities.extract(tokens[z]));

              atom = factory.Index(strings[0], restore());
              add(atom, tokens[0]);
           }
           break;
         case ParserConstants.IDEA_EXPR:                         // implemented
           parseIdea(ParserUtilities.extract(tokens[0]));
           break;
         case ParserConstants.EXPR_EVAL_STRING:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           datum.setType(ParserConstants.IDEA_STRING);
           parse(datum);

           atom = factory.Call("__EXEC__");
           add(atom, tokens[0]);
           break;
         case ParserConstants.IDEA_STRING: // implemented -- parsed literals, one of my favorite features in sleep

           /** create a frame, we assume the PLiteral machine will destroy it */
           atom = factory.CreateFrame();
           add(atom, tokens[0]);
           
           bool isVar = false; // is the current buffer d a varname or not?

           String varname, align; // some temp vars we'll use later...
           
           java.lang.StringBuffer d = new java.lang.StringBuffer(); // the string buffer where we will dump our results.

           ll = new java.util.LinkedList<Object>();
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
                           int codepoint = java.lang.Integer.parseInt(mutilate, 16);
                           d.append((char)codepoint);
                        }
                        catch (java.lang.NumberFormatException nex)
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
                           int codepoint = java.lang.Integer.parseInt(mutilate, 16);
                           d.append((char)codepoint);
                        }
                        catch (java.lang.NumberFormatException nex)
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

                  if (ops.Length == 3)
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
                  d       = new java.lang.StringBuffer();
              }
              else if (current == '$' && !Checkers.isEndOfVar(si.peek()) && si.hasNext())
              {
                  ll.add(PLiteral.fragment(PLiteral.STRING_FRAGMENT, d.toString()));
                  d = new java.lang.StringBuffer();
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
         case ParserConstants.HACK_INC: // implemented
           mutilate = strings[0].substring(0, strings[0].length() - 2);
           parseBlock(new Token(mutilate + " = " + mutilate + " + 1;", tokens[0].getHint()));
           break;
         case ParserConstants.HACK_DEC: // implemented
           //
           // [TRANSFORM]: Reconstructing "+temp[0]+" deccrement hack
           //
           mutilate = strings[0].substring(0, strings[0].length() - 2);
           parseBlock(new Token(mutilate + " = " + mutilate + " - 1;", tokens[0].getHint()));
           break;
         case ParserConstants.EXPR_BIND_PRED:
           //
           // [BIND PREDICATE FUNCTION]: "+temp[0]+" "+temp[1]);
           //
           backup();
           parseBlock(tokens[2]);
           atom = factory.BindPredicate(strings[0], parsePredicate(ParserUtilities.extract(tokens[1])), restore());
           add(atom, tokens[0]);
           break; 
         case ParserConstants.EXPR_BIND_FILTER:
           //
           // [BIND PREDICATE FUNCTION]: on | EVENT | expression | { code }
           //

           backup();
           parseBlock(tokens[3]);
           b = restore();

           atom = factory.BindFilter(strings[0], strings[1], b, strings[2]);
           add(atom, tokens[0]);
           break; 
         case ParserConstants.EXPR_BIND: // implemented
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
         case ParserConstants.EXPR_TRYCATCH: 
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
         case ParserConstants.EXPR_BLOCK:  // implemented
           parseBlock(ParserUtilities.extract(tokens[0]));
           break;
         case ParserConstants.IDEA_BLOCK:  // turns our block into a scalar :)
           backup();

           parseBlock(ParserUtilities.extract(tokens[0]));

           atom    = factory.CreateClosure(restore());
           add(atom, tokens[0]);
           break;
         case ParserConstants.IDEA_FUNC: // implemented 
           TokenList funcParms = LexicalAnalyzer.CreateTerms(parser, new StringIterator(strings[0], tokens[0].getHint()));

           strings = funcParms.getStrings(); 
           tokens  = funcParms.getTokens();

           if (strings[0].charAt(0) != '&')
           {
              strings[0] = '&' + strings[0];
           }

           if ((strings[0].equals("&iff") || strings[0].equals("&?")) && tokens.Length > 1)
           {
              TokenList terms = ParserUtilities.groupByParameterTerm(parser, ParserUtilities.extract(tokens[1]));
              Token[] termsAr = terms.getTokens();

              backup();
              if (termsAr.Length >= 2)
              {
                 parseIdea(termsAr[1]);
              }
              else
              {
                 parseIdea(termsAr[0].copy("true"));
              }
              a = restore();

              backup();
              if (termsAr.Length == 3)
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
           else if (tokens.Length > 1)
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
         case ParserConstants.EXPR_WHILE:                                        // done
           backup();
           parseBlock(tokens[2]);    
           atom = factory.Goto(parsePredicate(ParserUtilities.extract(tokens[1])), restore(), null);
           add(atom, tokens[1]);
           break;
         case ParserConstants.EXPR_WHILE_SPECIAL:                                        // done
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
         case ParserConstants.EXPR_ASSIGNMENT_T:                                  // implemented
         case ParserConstants.EXPR_ASSIGNMENT_T_OP:
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           TokenList terms2 = ParserUtilities.groupByParameterTerm(parser, ParserUtilities.extract(tokens[0]));
           Token[] termsAr2 = terms2.getTokens();

           for (int x = 0; x < termsAr2.Length; x++)
           {
              parseIdea(termsAr2[x]);
           }

           parseIdea(tokens[2]);

           if (datum.getType() == ParserConstants.EXPR_ASSIGNMENT_T_OP)
           {
              atom = factory.AssignTupleAndOperate(strings[1].substring(0, strings[1].Length - 1));
           }
           else
           {
              atom = factory.AssignT();
           }
           add(atom, tokens[0]);
           break;
         case ParserConstants.EXPR_ASSIGNMENT:                                  // implemented
         case ParserConstants.EXPR_ASSIGNMENT_OP:                                  // implemented
           atom = factory.CreateFrame();
           add(atom, tokens[2]);

           parseIdea(tokens[2]);

           backup();
           parseIdea(tokens[0]);

           if (datum.getType() == ParserConstants.EXPR_ASSIGNMENT_OP)
           {
              atom = factory.AssignAndOperate(restore(), strings[1].substring(0, strings[1].Length - 1));
           }
           else
           {
              atom = factory.Assign(restore());
           }
           add(atom, tokens[2]);
           break;
         case ParserConstants.EXPR_IF_ELSE:                                // done
           //
           // if <cond> <block> else -do this again-
           //
           // parse an if-else statement.

           backup();
           parseBlock(tokens[2]);
           a = restore();

           backup();
           if (tokens.Length >= 4)
           {
              if (strings[4].equals("if"))
              {
                 parseBlock(ParserUtilities.join(ParserUtilities.get(tokens, 4, tokens.Length)));
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
         case ParserConstants.EXPR_FOREACH_SPECIAL:
           // |foreach   0
           // |$key      1
           // |=>        2
           // |$value    3
           // |(@temp)   4
           // |{ &printf("hi"); } 5
 
           /**** purposeful fall thru... ****/

         case ParserConstants.EXPR_FOREACH:
           // |foreach
           // |$var
           // |(@temp)
           // |{ &printf("hi"); }

           //
           // setup our frame with the value, possibly the key,  and the source
           //
           atom = factory.CreateFrame();
           add(atom, tokens[0]);

           if (datum.getType() == ParserConstants.EXPR_FOREACH)
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

           if (datum.getType() == ParserConstants.EXPR_FOREACH)
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
         case ParserConstants.EXPR_FOR:
           // |for
           // |($x = 0; $x < 100; $x++)
           // |{ &printf("hi"); }

           Token [] extracted_terms = ParserUtilities.groupByBlockTerm(parser, ParserUtilities.extract(tokens[1])).getTokens();

           //
           // evaluate initial terms...           
           //
           java.lang.StringBuffer doThis = new java.lang.StringBuffer();

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

           if (extracted_terms.Length == 3)
           {
              backup();
              doThis = new java.lang.StringBuffer();

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
              doThis = new java.lang.StringBuffer();
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
              if (strings.Length == 1)
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
           catch (java.lang.Exception ex)
           {
              if (tokens.Length == 2)
              {
                 parser.reportError(ex.getMessage(), ParserUtilities.makeToken("using  " + strings[0] + " from: " + strings[1], tokens[1]));
              }
              else
              {
                 parser.reportError(ex.getMessage(), ParserUtilities.makeToken("using  " + strings[0], tokens[0]));
              }
           }
           break;           
         case ParserConstants.EXPR_ASSERT:
           if (tokens.Length == 1)
           {
              parser.reportError("Assertion can't be empty!", tokens[0]);
              return;
           }

           if (bool.Parse(java.lang.SystemJ.getProperty("sleep.assert", "true")).Equals(Boolean.FalseString.ToLower()))
           {
              return;
           }

           Token [] assert_terms = ParserUtilities.groupByMessageTerm(parser, tokens[1]).getTokens();
           
           backup();
              atom = factory.CreateFrame();
              add(atom, tokens[0]);

              if (assert_terms.Length == 1)
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
         case ParserConstants.EXPR_RETURN:                     // implemented
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
           else if (tokens.Length >= 2)
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
           break;
      }     
   }

   public void parseParameters(Token token)
   {
      TokenList terms   = ParserUtilities.groupByParameterTerm(parser, token);
      Token[]   termsAr = terms.getTokens();

      for (int x = termsAr.Length - 1; x >= 0; x--)
      {
         parseIdea(termsAr[x]);
      }
   }
}

}