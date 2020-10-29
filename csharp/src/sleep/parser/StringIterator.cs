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


public class StringIterator
{
   protected int    position = 0;
   protected int    lineNo;
   protected char[] text;
   protected String texts;
   protected int    begin    = 0;

   public StringIterator(String text)
   {
      this(text, 0);
   }

   public String toString()
   {
      return texts;
   }

   public StringIterator(String _text, int _lineNo)
   {
      texts  = _text;
      text   = _text.toCharArray();
      lineNo = _lineNo;
   }

   /** check that there is another character out there for us to get */
   public bool hasNext()
   {
      return position < text.Length;
   }

   /** check that there are at least n chars we can still get */
   public bool hasNext(int n)
   {
      return (position + n - 1) < text.Length;
   }

   public int getLineNumber()
   {
      return lineNo;
   }

   public Token getErrorToken()
   {
      return new Token(getEntireLine(), getLineNumber(), getLineMarker());
   }
  
   public String getEntireLine()
   {
      int temp = position;
      while (temp < text.Length && text[temp] != '\n')
      {
         temp++;
      }

      return texts.substring(begin, temp);
   }

   public int getLineMarker()
   {
      return position - begin;
   }

   public bool isNextString(String n)
   {
      return ((position + n.length()) <= text.Length) && texts.substring(position, position + n.length()).equals(n);
   }

   public bool isNextChar(char n)
   {
      return hasNext() && text[position] == n;
   }

   public char peek()
   {
      return hasNext() ? text[position] : (char)0; 
   }

   /** does a direct skip of n characters, use only when you know what the chars are.. this will not increment the line number counter */
   public void skip(int n)
   {
      position += n;
   }

   /** returns the string consisting of the next n characters. */
   public String next(int n)
   {
      java.lang.StringBuffer buffer = new java.lang.StringBuffer();

      for (int x = 0; x < n; x++)
      {
         buffer.append(next());
      }

      return buffer.toString();
   }

   /** moves the iterator forward one char */
   public char next()
   {
      char current = text[position];

      if (position > 0 && text[position - 1] == '\n')
      {
         lineNo++;
         begin = position;
      }

      position++;

      return current;
   }

   public void mark()
   {
      mark1.add(0, new java.lang.Integer(position));
      mark2.add(0, new java.lang.Integer(lineNo));
   }

   public String reset()
   {
      java.lang.Integer temp1 = (java.lang.Integer)mark1.removeFirst();
      java.lang.Integer temp2 = (java.lang.Integer)mark2.removeFirst();
//      position = temp1.intValue();
//      lineNo   = temp2.intValue();

      return texts.substring(temp1.intValue(), position);
   }

   protected java.util.LinkedList<Object> mark1 = new java.util.LinkedList<Object>();
   protected java.util.LinkedList<Object> mark2 = new java.util.LinkedList<Object>();
 

   public static void main(String[] args)
   {
      StringIterator temp = new StringIterator(args[0]);
      
      java.lang.StringBuffer blah = new java.lang.StringBuffer();
      while (temp.hasNext())
      {
         char t = temp.next();
         blah.append(t);
         if (t == '\n')
         {
            java.lang.SystemJ.outJ.print(temp.getLineNumber() + ": " + blah.toString());
            blah = new java.lang.StringBuffer();
         }
      }
   }
}
}