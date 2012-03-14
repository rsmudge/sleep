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
package sleep.console;

/** a necessary interface for creating a front end to the sleep console.  all messages read in or written out from the console 
are done through a ConsoleProxy implementation. */
public interface ConsoleProxy
{
   /**
    * print a message to the console with no newline
    * @param message the message to print
    */
   public void consolePrint (String message);

   /**
    * print a message to the console with a newline
    * @param message the message to print
    */
   public void consolePrintln (Object message);

   /** read a message in from the console.  this method should block until a line is read. */
   public String consoleReadln ();
}
