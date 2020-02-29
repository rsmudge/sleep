/*
 * Copyright 2002-2020 Raphael Mudge
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


