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

public class TokenList
{
   protected java.util.LinkedList<Object> terms  = new java.util.LinkedList<Object>();
   protected String[]   sarray = null;
   protected Token[]    tarray = null;

   public void add(Token temp)
   {
      terms.add(temp);
   }

   public String toString()
   {
      java.lang.StringBuffer rv = new java.lang.StringBuffer();

      java.util.Iterator<Object> i = terms.iterator();
      while (i.hasNext())
      {
         rv.append(i.next().toString());
         rv.append(" ");
      }

      return rv.toString();
   }

   public java.util.LinkedList<Object> getList()
   {
      return terms;
   }

   private static readonly Token[]  dummyT = new Token[0];
   private static readonly String[] dummyS = new String[0];

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
         sarray = new String[temp.Length];
         for (int x = 0; x < temp.Length; x++)
         {
            sarray[x] = temp[x].toString();
         }
      }
      return sarray;
   }
}
}