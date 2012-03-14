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
package sleep.bridges.io;

import java.io.*;
import sleep.bridges.BridgeUtilities;
import sleep.runtime.ScriptEnvironment;

public class FileObject extends IOObject
{
   protected File file;

   /** returns the file referenced by this IOObject */
   public Object getSource()
   {
      return file;
   }

   /** opens a file and references it to this file object.  the descriptor parameter is a filename */
   public void open(String descriptor, ScriptEnvironment env)
   {
      try
      {
         if (descriptor.charAt(0) == '>' && descriptor.charAt(1) == '>')
         {
            file = BridgeUtilities.toSleepFile(descriptor.substring(2, descriptor.length()).trim(), env.getScriptInstance());
            openWrite(new FileOutputStream(file, true));
         }
         else if (descriptor.charAt(0) == '>')
         {
            file = BridgeUtilities.toSleepFile(descriptor.substring(1, descriptor.length()).trim(), env.getScriptInstance());
            openWrite(new FileOutputStream(file, false));
         }
         else
         {
            file = BridgeUtilities.toSleepFile(descriptor, env.getScriptInstance());
            openRead(new FileInputStream(file));
         }
      }
      catch (Exception ex)
      {
         env.flagError(ex);
      }
   }
}
