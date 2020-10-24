/*
 * Copyright 2002-2020 Raphael Mudge
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
package sleep.parser;

import java.util.*;

/**
 * A class that provides a bunch of static methods for checking a stream of
 * sleep tokens for a certain lexical structure.  
 */
public class Checkers 
{
   /** a hashtable that keeps track of language keywords so they are not mistaken for function names */
   protected static Hashtable keywords;

   public static void addKeyword(String keyword)
   {
      keywords.put(keyword, Boolean.TRUE);
   }

   static
   {
      keywords = new Hashtable();

      keywords.put("if",       Boolean.TRUE);
      keywords.put("for",      Boolean.TRUE);
      keywords.put("while",    Boolean.TRUE);
      keywords.put("foreach",  Boolean.TRUE);
      keywords.put("&&",       Boolean.TRUE);
      keywords.put("||",       Boolean.TRUE);
      keywords.put("EOT",      Boolean.TRUE);
      keywords.put("EOL",      Boolean.TRUE);
      keywords.put("return",   Boolean.TRUE);
      keywords.put("halt",     Boolean.TRUE);
      keywords.put("done",     Boolean.TRUE);
      keywords.put("break",    Boolean.TRUE);
      keywords.put("continue", Boolean.TRUE);
      keywords.put("yield",    Boolean.TRUE);
      keywords.put("throw",    Boolean.TRUE);
      keywords.put("try",      Boolean.TRUE);
      keywords.put("catch",    Boolean.TRUE);
      keywords.put("assert",   Boolean.TRUE);
   }

   public static boolean isIfStatement(String a, String b, String c)
   {
      return (a.toString().equals("if") && isExpression(b.toString()) && isBlock(c.toString()));
   }

   public static boolean isElseStatement(String a, String b)
   {
      return (a.equals("else") && isBlock(b));
   }

   public static boolean isElseIfStatement(String a, String b, String c, String d)
   {
      return (a.equals("else") && isIfStatement(b, c, d));
   }

   public static final boolean isIncrementHack (String a)
   {
       return (isScalar(a) && a.length() > 3 && (a.substring(a.length() - 2, a.length()).equals("++")));
   }

   public static final boolean isDecrementHack (String a)
   {
       return (isScalar(a) && a.length() > 3 && (a.substring(a.length() - 2, a.length()).equals("--")));
   }
   
   public static final boolean isObjectNew(String a, String b)
   {
       return (a.equals("new"));
   }

   public static final boolean isClosureCall(String a, String b)
   {
       return (b.equals("EOT"));
   }

   public static final boolean isImportStatement(String a, String b)
   {
       return (a.equals("import"));
   }

   public static final boolean isClassLiteral(String a)
   {
       return a.length() >= 2 && a.charAt(0) == '^';
   }

   public static final boolean isClassPiece(String a)
   {
       if (a.length() >= 1 && !isVariable(a) && Character.isJavaIdentifierStart(a.charAt(0)))
       {
          for (int x = 1; x < a.length(); x++)
          {
             if (!Character.isJavaIdentifierPart(a.charAt(x)) && a.charAt(x) != '.')
             {
                return false;
             }
          }
          return true;
       }
       return false;
   }

   public static final boolean isClassIdentifier(Parser parser, String a)
   {
       return isClassPiece(a) && parser.findImportedClass(a) != null;
   }

   public static final boolean isBindFilter(String a, String b, String c, String d)
   {
      return (isBlock(d));
   }

   public static final boolean isBindPredicate(String a, String b, String c)
   {
      return (isExpression(b) && isBlock(c));
   }

   public static final boolean isBind(String a, String b, String c)
   {
      return (!b.equals("=") && isBlock(c));
   }

   public static boolean isHash(String a)
   {
      return (a.charAt(0) == '%');
   }

   public static boolean isArray(String a)
   {
      return (a.charAt(0) == '@');
   }

   public static boolean isFunctionReferenceToken(String a)
   {
      return (a.charAt(0) == '&' && a.length() > 1 && !a.equals("&&"));
   }

   public static final boolean isVariableReference (String temp)
   {
      return temp.length() >= 3 && temp.charAt(0) == '\\' && !temp.equals("\\$null") && isVariable(temp.substring(1));
   }

   public static final boolean isVariable (String temp)
   {
      return (isScalar(temp) || isHash(temp) || isArray(temp));
   }

   public static final boolean isScalar (String temp)
   {
      return (temp.charAt(0) == '$');
   }

   public static boolean isIndex(String a)
   {
      return (a.charAt(0) == '[' && a.charAt(a.length() - 1) == ']');
   }

   public static boolean isExpression(String a)
   {
      return (a.charAt(0) == '(' && a.charAt(a.length() - 1) == ')');
   }

   public static boolean isBlock(String a)
   {
      return (a.charAt(0) == '{' && a.charAt(a.length() - 1) == '}');
   }

   public static boolean isFunctionCall(String a, String b)
   {
      return ((isFunction(a) || a.equals("@") || a.equals("%")) && isExpression(b));
   }

   public static boolean isFunction(String a)
   {
      return ((Character.isJavaIdentifierStart(a.charAt(0)) || a.charAt(0) == '&') && a.charAt(0) != '$' && keywords.get(a) == null);
   }

   public static boolean isDataLiteral(String a)
   {
      return (a.length() > 2) && (  a.substring(0, 2).equals("@(") || a.substring(0, 2).equals("%(")  );
   }

   public static boolean isFunctionCall(String a)
   {
      return ((isFunction(a) || isDataLiteral(a)) && (a.indexOf('(') > -1) && (a.indexOf(')') > -1));
   }

   public static boolean isIndexableItem(String a, String b)
   {
      return (isIndex(b) && (isFunctionCall(a) || isExpression(a) || isVariable(a) || isIndex(a) || isFunctionReferenceToken(a) || isBacktick(a)));
   }

   public static boolean isIndexableItem(String a)
   {
      if (a.charAt(a.length() - 1) == ']')
      {
         int idx = a.lastIndexOf('[');
         if (idx > 0)
             return isIndexableItem(a.substring(0, idx), a.substring(idx, a.length()));  
      }

      return false;
   }

   public static boolean isHashIndex(String a)
   {
      return (isHash(a) && (a.indexOf('[') > -1) && (a.indexOf(']') > -1));
   }

   public static boolean isArrayIndex(String a)
   {
      return (isArray(a) && (a.indexOf('[') > -1) && (a.indexOf(']') > -1));
   }

   public static boolean isOperator(String a, String b, String c)
   {
       return true;
   }

   public static final boolean isSpecialWhile(String a, String b, String c, String d)
   {
       return isWhile(a, c, d) && isVariable(b);
   }

   public static final boolean isWhile (String a, String b, String c)
   {
       return (a.equals("while") && isExpression(b) && isBlock(c));
   }

   public static final boolean isFor (String a, String b, String c)
   {
       return (a.equals("for") && isExpression(b) && isBlock(c));
   }

   public static final boolean isTryCatch (String a, String b, String c, String d, String e)
   {
       return a.equals("try") && c.equals("catch") && isBlock(b) && isBlock(e) && isScalar(d);
   }
  
   public static final boolean isForeach (String a, String b, String c, String d)
   {
       return (a.equals("foreach") && isVariable(b) && isExpression(c) && isBlock(d));
   }

   public static final boolean isSpecialForeach (String a, String b, String c, String d, String e, String f)
   {
       return (a.equals("foreach") && isVariable(b) && c.equals("=>") && isVariable(d) && isExpression(e) && isBlock(f));
   }

   public static final boolean isAssert (String temp)
   {
       return (temp.equals("assert"));
   }

   public static final boolean isReturn (String temp)
   {
       // halt and done are kind of jIRC related... when you write the scripting language you
       // can do whatever you want...
       return  (temp.equals("return") || temp.equals("done") || temp.equals("halt") || temp.equals("break") || temp.equals("yield") || temp.equals("continue") || temp.equals("throw") || temp.equals("callcc"));
   }

   public static final boolean isString (String item)
   {
      return (item.charAt(0) == '\"' && item.charAt(item.length()-1) == '\"');
   }

   public static final boolean isBacktick (String item)
   {
      return (item.charAt(0) == '`' && item.charAt(item.length()-1) == '`');
   }

   public static final boolean isLiteral (String item)
   {
      return (item.charAt(0) == '\'' && item.charAt(item.length()-1) == '\'');
   }

   public static final boolean isNumber (String temp)
   {
      try
      {
         if (temp.endsWith("L"))
         {
            temp = temp.substring(0, temp.length() - 1);
            Long.decode(temp);
         }
         else
         {
            Integer.decode(temp);
         }
      }
      catch (Exception hex) 
      {
         return false;
      }
      return true;
   }

   public static final boolean isDouble (String temp)
   {
      try
      {
         Double.parseDouble(temp);
      }
      catch (Exception hex) 
      {
         return false;
      }
      return true;
   }

   public static final boolean isBoolean (String temp)
   {
      return (temp.equals("true") || temp.equals("false"));
   }

   public static final boolean isBiPredicate (String a, String b, String c)
   {
      return true;
   }

   public static final boolean isUniPredicate (String a, String b)
   {
      return (a.charAt(0) == '-') || (a.length() > 1 && a.charAt(0) == '!' && a.charAt(1) == '-');
   }

   public static final boolean isAndPredicate (String a, String b, String c)
   {
      return (b.equals("&&"));
   }

   public static final boolean isOrPredicate (String a, String b, String c)
   {
      return (b.equals("||"));
   }

   public static final boolean isComment (String a)
   {
      return (a.charAt(0) == '#' && a.charAt(a.length() - 1) == '\n');
   }

   public static final boolean isEndOfVar(char n)
   {
      return n == ' ' || n == '\t' || n == '\n' || n == '$' || n == '\\';
   }
}



