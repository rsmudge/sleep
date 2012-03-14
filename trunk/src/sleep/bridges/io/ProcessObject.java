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
import sleep.runtime.*;

public class ProcessObject extends IOObject
{
   protected Process process;

   /** returns the Process object used by this IO implementation */
   public Object getSource()
   {
      return process;
   }

   public void open(String command[], String[] environment, File startDir, ScriptEnvironment env)
   {
      try
      {
         if (command.length > 0)
         {
            String args;
            command[0] = command[0].replace('/', File.separatorChar);
         }

         process = Runtime.getRuntime().exec(command, environment, startDir);

         openRead(process.getInputStream());
         openWrite(process.getOutputStream());
      }
      catch (Exception ex)
      {
         env.flagError(ex);
      }
   }

   public Scalar wait(ScriptEnvironment env, long timeout)
   {
      if (getThread() != null && getThread().isAlive())
      {
         super.wait(env, timeout);
      }

      try
      {
         process.waitFor();
         return SleepUtils.getScalar(process.waitFor());
      }
      catch (Exception ex)
      {
         env.flagError(ex);
      }

      return SleepUtils.getEmptyScalar();
   }

   public void close()
   {
      super.close();
      process.destroy();
   }
}


