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
