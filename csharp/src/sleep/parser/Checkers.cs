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


/**
 * A class that provides a bunch of static methods for checking a stream of
 * sleep tokens for a certain lexical structure.  
 */
public class Checkers 
{
   /** a hashtable that keeps track of language keywords so they are not mistaken for function names */
   protected static java.util.Hashtable<Object,Object> keywords;

   public static void addKeyword(String keyword)
   {
      keywords.put(keyword, Boolean.TRUE);
   }

   static Checkers()
   {
      keywords = new java.util.Hashtable<Object,Object>();

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

   public static bool isIfStatement(String a, String b, String c)
   {
      return (a.toString().equals("if") && isExpression(b.toString()) && isBlock(c.toString()));
   }

   public static bool isElseStatement(String a, String b)
   {
      return (a.equals("else") && isBlock(b));
   }

   public static bool isElseIfStatement(String a, String b, String c, String d)
   {
      return (a.equals("else") && isIfStatement(b, c, d));
   }

   public static bool isIncrementHack (String a)
   {
       return (isScalar(a) && a.length() > 3 && (a.substring(a.length() - 2, a.length()).equals("++")));
   }

   public static bool isDecrementHack (String a)
   {
       return (isScalar(a) && a.length() > 3 && (a.substring(a.length() - 2, a.length()).equals("--")));
   }
   
   public static bool isObjectNew(String a, String b)
   {
       return (a.equals("new"));
   }

   public static bool isClosureCall(String a, String b)
   {
       return (b.equals("EOT"));
   }

   public static bool isImportStatement(String a, String b)
   {
       return (a.equals("import"));
   }

   public static bool isClassLiteral(String a)
   {
       return a.length() >= 2 && a.charAt(0) == '^';
   }

   public static bool isClassPiece(String a)
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

   public static bool isClassIdentifier(Parser parser, String a)
   {
       return isClassPiece(a) && parser.findImportedClass(a) != null;
   }

   public static bool isBindFilter(String a, String b, String c, String d)
   {
      return (isBlock(d));
   }

   public static bool isBindPredicate(String a, String b, String c)
   {
      return (isExpression(b) && isBlock(c));
   }

   public static bool isBind(String a, String b, String c)
   {
      return (!b.equals("=") && isBlock(c));
   }

   public static bool isHash(String a)
   {
      return (a.charAt(0) == '%');
   }

   public static bool isArray(String a)
   {
      return (a.charAt(0) == '@');
   }

   public static bool isFunctionReferenceToken(String a)
   {
      return (a.charAt(0) == '&' && a.length() > 1 && !a.equals("&&"));
   }

   public static bool isVariableReference (String temp)
   {
      return temp.length() >= 3 && temp.charAt(0) == '\\' && !temp.equals("\\$null") && isVariable(temp.substring(1));
   }

   public static bool isVariable (String temp)
   {
      return (isScalar(temp) || isHash(temp) || isArray(temp));
   }

   public static bool isScalar (String temp)
   {
      return (temp.charAt(0) == '$');
   }

   public static bool isIndex(String a)
   {
      return (a.charAt(0) == '[' && a.charAt(a.length() - 1) == ']');
   }

   public static bool isExpression(String a)
   {
      return (a.charAt(0) == '(' && a.charAt(a.length() - 1) == ')');
   }

   public static bool isBlock(String a)
   {
      return (a.charAt(0) == '{' && a.charAt(a.length() - 1) == '}');
   }

   public static bool isFunctionCall(String a, String b)
   {
      return ((isFunction(a) || a.equals("@") || a.equals("%")) && isExpression(b));
   }

   public static bool isFunction(String a)
   {
      return ((Character.isJavaIdentifierStart(a.charAt(0)) || a.charAt(0) == '&') && a.charAt(0) != '$' && keywords.get(a) == null);
   }

   public static bool isDataLiteral(String a)
   {
      return (a.length() > 2) && (  a.substring(0, 2).equals("@(") || a.substring(0, 2).equals("%(")  );
   }

   public static bool isFunctionCall(String a)
   {
      return ((isFunction(a) || isDataLiteral(a)) && (a.indexOf('(') > -1) && (a.indexOf(')') > -1));
   }

   public static bool isIndexableItem(String a, String b)
   {
      return (isIndex(b) && (isFunctionCall(a) || isExpression(a) || isVariable(a) || isIndex(a) || isFunctionReferenceToken(a) || isBacktick(a)));
   }

   public static bool isIndexableItem(String a)
   {
      if (a.charAt(a.length() - 1) == ']')
      {
         int idx = a.lastIndexOf('[');
         if (idx > 0)
             return isIndexableItem(a.substring(0, idx), a.substring(idx, a.length()));  
      }

      return false;
   }

   public static bool isHashIndex(String a)
   {
      return (isHash(a) && (a.indexOf('[') > -1) && (a.indexOf(']') > -1));
   }

   public static bool isArrayIndex(String a)
   {
      return (isArray(a) && (a.indexOf('[') > -1) && (a.indexOf(']') > -1));
   }

   public static bool isOperator(String a, String b, String c)
   {
       return true;
   }

   public static bool isSpecialWhile(String a, String b, String c, String d)
   {
       return isWhile(a, c, d) && isVariable(b);
   }

   public static bool isWhile (String a, String b, String c)
   {
       return (a.equals("while") && isExpression(b) && isBlock(c));
   }

   public static bool isFor (String a, String b, String c)
   {
       return (a.equals("for") && isExpression(b) && isBlock(c));
   }

   public static bool isTryCatch (String a, String b, String c, String d, String e)
   {
       return a.equals("try") && c.equals("catch") && isBlock(b) && isBlock(e) && isScalar(d);
   }
  
   public static bool isForeach (String a, String b, String c, String d)
   {
       return (a.equals("foreach") && isVariable(b) && isExpression(c) && isBlock(d));
   }

   public static bool isSpecialForeach (String a, String b, String c, String d, String e, String f)
   {
       return (a.equals("foreach") && isVariable(b) && c.equals("=>") && isVariable(d) && isExpression(e) && isBlock(f));
   }

   public static bool isAssert (String temp)
   {
       return (temp.equals("assert"));
   }

   public static bool isReturn (String temp)
   {
       // halt and done are kind of jIRC related... when you write the scripting language you
       // can do whatever you want...
       return  (temp.equals("return") || temp.equals("done") || temp.equals("halt") || temp.equals("break") || temp.equals("yield") || temp.equals("continue") || temp.equals("throw") || temp.equals("callcc"));
   }

   public static bool isString (String item)
   {
      return (item.charAt(0) == '\"' && item.charAt(item.length()-1) == '\"');
   }

   public static bool isBacktick (String item)
   {
      return (item.charAt(0) == '`' && item.charAt(item.length()-1) == '`');
   }

   public static bool isLiteral (String item)
   {
      return (item.charAt(0) == '\'' && item.charAt(item.length()-1) == '\'');
   }

   public static bool isNumber (String temp)
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
      catch (java.lang.Exception hex) 
      {
         return false;
      }
      return true;
   }

   public static bool isDouble (String temp)
   {
      try
      {
         Double.parseDouble(temp);
      }
      catch (java.lang.Exception hex) 
      {
         return false;
      }
      return true;
   }

   public static bool isBoolean (String temp)
   {
      return (temp.equals("true") || temp.equals("false"));
   }

   public static bool isBiPredicate (String a, String b, String c)
   {
      return true;
   }

   public static bool isUniPredicate (String a, String b)
   {
      return (a.charAt(0) == '-') || (a.length() > 1 && a.charAt(0) == '!' && a.charAt(1) == '-');
   }

   public static bool isAndPredicate (String a, String b, String c)
   {
      return (b.equals("&&"));
   }

   public static bool isOrPredicate (String a, String b, String c)
   {
      return (b.equals("||"));
   }

   public static bool isComment (String a)
   {
      return (a.charAt(0) == '#' && a.charAt(a.length() - 1) == '\n');
   }

   public static bool isEndOfVar(char n)
   {
      return n == ' ' || n == '\t' || n == '\n' || n == '$' || n == '\\';
   }
}



}