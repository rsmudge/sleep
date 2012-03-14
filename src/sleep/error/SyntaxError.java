/* 
 * Copyright (C) 2002-2012 Raphael Mudge (rsmudge@gmail.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package sleep.error;

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
   {
      this(_description, _code, _lineNo, null);
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
