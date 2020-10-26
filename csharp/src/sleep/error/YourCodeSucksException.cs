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

using  java.util;
using  java.io;

namespace sleep.error{

/**
 * Syntax errors are a reality of programming.  Any time a syntax error occurs when attempting to load a script the 
 * exception YourCodeSucksException will be raised.  [ yes, this exception name is staying ]
 * <br>
 * <br>To catch a YourCodeSucksException:
 * <br>
 * <pre>
 * try
 * {
 *    ScriptInstance script;
 *    script = loader.loadScript("name", inputStream);
 * }
 * catch (YourCodeSucksException ex)
 * {
 *    Iterator i = ex.getErrors().iterator();
 *    while (i.hasNext())
 *    {
 *       SyntaxError error = (SyntaxError)i.next();
 * 
 *       String description = error.getDescription();
 *       String code        = error.getCodeSnippet();
 *       int    lineNumber  = error.getLineNumber();
 *    }
 * }
 * </pre>
 * 
 * @see sleep.error.SyntaxError
 */
public class YourCodeSucksException : RuntimeException
{
    LinkedList allErrors;

    /** Initialize the exception (sleep parser) */
    public YourCodeSucksException(LinkedList myErrors)
    {
       allErrors = myErrors;
    }

    /** Returns a minimal string representation of the errors within this exception */
    public String getMessage()
    {
       StringBuffer buf = new StringBuffer(allErrors.size() + " error(s): ");

       Iterator i = getErrors().iterator();
       while (i.hasNext())
       {
          SyntaxError temp = (SyntaxError)i.next();

          buf.append(temp.getDescription());
          buf.append(" at " + temp.getLineNumber());
     
          if (i.hasNext())
             buf.append("; ");
       }

       return buf.toString();
    }

    /** Returns a simple string representation of the errors within this exception */
    public String toString()
    {
       return "YourCodeSucksException: " + getMessage();
    }

    /** print a nicely formatted version of the script errors to the specified stream */
    public void printErrors(OutputStream outJ)
    {
       PrintWriter pout = new PrintWriter(outJ);
       pout.print(formatErrors());
       pout.flush();
    }

    /** generate a nicely formatted string representation of the script errors in this exception */
    public String formatErrors()
    {
       StringBuffer representation = new StringBuffer();

       LinkedList errors = getErrors();
       Iterator i = errors.iterator();
       while (i.hasNext())
       {
           SyntaxError anError = (SyntaxError)i.next();
           representation.append("Error: " + anError.getDescription() + " at line " + anError.getLineNumber() + "\n");
           representation.append("       " + anError.getCodeSnippet() + "\n");

           if (anError.getMarker() != null)
             representation.append("       " + anError.getMarker() + "\n");
       }

       return representation.toString();
    }

    /** All of the errors are stored in a linked list.  The linked list contains {@link sleep.error.SyntaxError SyntaxError} objects. */
    public LinkedList getErrors()
    {
       return allErrors;
    }
}
}