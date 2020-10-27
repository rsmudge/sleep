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

using  sleep.error;

namespace sleep.parser{

public class CommentRule : Rule
{
   public int getType() { return PRESERVE_SINGLE; }
    
   public String toString()
   {
      return "Comment parsing information";
   }

   public String wrap(String value)
   {
      StringBuffer rv = new StringBuffer(value.length() + 2);
      rv.append('#');
      rv.append(value);
      rv.append('\n');

      return rv.toString();
   }

   public bool isLeft(char n) { return (n == '#'); }
   public bool isRight(char n) { return (n == '\n'); }
   public bool isMatch(char n) { return false; }

   public bool isBalanced()
   {
      return true;
   }

   public Rule copyRule()
   {
      return this;  // we're safe doing this since comment rules contain no state information.
   }

   /** Used to keep track of opening braces to check balance later on */
   public void witnessOpen(Token token) { }

   /** Used to keep track of closing braces to check balance later on */
   public void witnessClose(Token token) { }
}
}