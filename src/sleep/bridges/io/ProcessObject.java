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


