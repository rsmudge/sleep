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
 
namespace sleep.error{

/** A class containing syntax error information.  A SyntaxError object is passed by a YourCodeSucksException.   
  *
  * @see sleep.error.YourCodeSucksException
  */
public class SyntaxError
{
   protected String description;
   protected String code;
   protected String marker;
   protected int    lineNo;

   /** construct a syntax error object, but enough about me... how about you? */
   public SyntaxError(String _description, String _code, int _lineNo)
   :
      this(_description, _code, _lineNo, null){
   }

   /** construct a syntax error object, but enough about me... how about you? */
   public SyntaxError(String _description, String _code, int _lineNo, String _marker)
   {
      description = _description;
      code        = _code;
      lineNo      = _lineNo;
      marker      = _marker;
   }

   /** return a marker string */
   public String getMarker()
   {
      return marker;
   }

   /** return a best guess description of what the error in the code might actually be */
   public String getDescription()  
   {
      return description;
   }

   /** return an isolated snippet of code from where the error occured */
   public String getCodeSnippet()
   {
      return code;
   }
 
   /** return the line number in the file where the error occured.  */
   public int getLineNumber()
   {
       return lineNo;
   }
}
}