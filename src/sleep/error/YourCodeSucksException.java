/*
   SLEEP - Simple Language for Environment Extension Purposes
 .-------------------------------------.
 | sleep.engine.YourCodeSucksException |______________________________________
 |                                                                            |
   Author: Raphael Mudge (rsmudge@mtu.edu)
           http://www.csl.mtu.edu/~rsmudge/
 
   Description: This class serves as a delivery agent for an error in code
     passed to the parser.
 
   * This software is distributed under the artistic license, see license.txt
     for more information. *
 
 |____________________________________________________________________________|
 */
package sleep.error;

import java.util.*;
import java.io.*;

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
public class YourCodeSucksException extends RuntimeException
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
    public void printErrors(OutputStream out)
    {
       PrintWriter pout = new PrintWriter(out);
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
