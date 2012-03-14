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

import java.io.*;
import java.util.*;

import sleep.engine.*;
import sleep.parser.*;
import sleep.runtime.*;
import sleep.error.*;

import sleep.taint.TaintUtils;

/** Default implementation of the console proxy class.  Provides a STDIN/STDOUT implementation of the sleep console. */
public class TextConsole implements ConsoleProxy
{
   public static void main(String args[])
   {
      ScriptLoader loader = new ScriptLoader();

      ConsoleImplementation temp = new ConsoleImplementation(null, null, loader);
      temp.setProxy(new TextConsole());

      if (args.length > 0)
      {
         boolean check = false;
         boolean ast   = false;
         boolean eval  = false;
         boolean expr  = false;
         boolean prof  = false;
         boolean time  = false;
         int     start = 0;

         while (start < args.length && (args[start].startsWith("--") || (args[start].length() >= 2 && args[start].charAt(0) == '-')))
         {
            if (args[start].equals("-version") || args[start].equals("--version") || args[start].equals("-v"))
            {
                System.out.println(SleepUtils.SLEEP_VERSION + " (" + SleepUtils.SLEEP_RELEASE + ")");
                return;
            } 
            else if (args[start].equals("-help") || args[start].equals("--help") || args[start].equals("-h"))
            {
                System.out.println(SleepUtils.SLEEP_VERSION + " (" + SleepUtils.SLEEP_RELEASE + ")");
                System.out.println("Usage: java [properties] -jar sleep.jar [options] [-|file|expression]");
                System.out.println("       properties:");
                System.out.println("         -Dsleep.assert=<true|false>");
                System.out.println("         -Dsleep.classpath=<path to locate 3rd party jars from>");
                System.out.println("         -Dsleep.debug=<debug level>");
                System.out.println("         -Dsleep.taint=<true|false>");
                System.out.println("       options:");
                System.out.println("         -a --ast       display the abstract syntax tree of the specified script");
                System.out.println("         -c --check     check the syntax of the specified file");
                System.out.println("         -e --eval      evaluate a script as specified on command line");
                System.out.println("         -h --help      display this help message");
                System.out.println("         -p --profile   collect and display runtime profile statistics");
                System.out.println("         -t --time      display total script runtime");
                System.out.println("         -v --version   display version information");
                System.out.println("         -x --expr      evaluate an expression as specified on the command line");
                System.out.println("       file:");
                System.out.println("         specify a '-' to read script from STDIN");
                return;
             }
             else if (args[start].equals("--check") || args[start].equals("-c"))
             {
                check = true;
             }
             else if (args[start].equals("--ast") || args[start].equals("-a"))
             {
                ast   = true;
             }
             else if (args[start].equals("--profile") || args[start].equals("-p"))
             {
                prof  = true;
             }
             else if (args[start].equals("--time") || args[start].equals("-t"))
             {
                time  = true;
             }
             else if (args[start].equals("--eval") || args[start].equals("-e"))
             {
                eval  = true;
             }
             else if (args[start].equals("--expr") || args[start].equals("-x"))
             {
                expr  = true;
             }
             else
             {
                System.err.println("Unknown argument: " + args[start]);
                return;
             }

             start++;
         }
         //
         // put all of our command line arguments into an array scalar
         //

         Scalar array = SleepUtils.getArrayScalar();
         for (int x = start + 1; x < args.length; x++)
         {
            array.getArray().push(TaintUtils.taint(SleepUtils.getScalar(args[x])));
         }

         try
         {
            ScriptInstance script;

            if (eval)
            {
                script = loader.loadScript(args[start - 1], args[start], new Hashtable());
            }
            else if (expr)
            {
                script = loader.loadScript(args[start - 1], "println(" + args[start] + ");", new Hashtable());
            }
            else if (args[start].equals("-"))
            {
                script = loader.loadScript("STDIN", System.in);
            }
            else
            {
                script = loader.loadScript(args[start]);     // load the script, parse it, etc.
            }

            script.getScriptVariables().putScalar("@ARGV", array);  // set @ARGV to be our array of command line arguments
            script.getScriptVariables().putScalar("$__SCRIPT__", SleepUtils.getScalar(script.getName()));

            if (System.getProperty("sleep.debug") != null)
            {
               script.setDebugFlags(Integer.parseInt(System.getProperty("sleep.debug")));
            }
          
            if (prof)
            {
               script.setDebugFlags(script.getDebugFlags() | 24);
            }

            if (check)
            {
               System.out.println(args[start] + " syntax OK");    
            }
            else if (ast)
            {
               System.out.println(script.getRunnableBlock());
            } 
            else
            {
               long beganAt = System.currentTimeMillis();

               script.runScript();                                     // run the script...

               if (prof)
               {
                  script.printProfileStatistics(System.out);
               }

               if (time)
               {
                   long difference = System.currentTimeMillis() - beganAt;
                   System.out.println("time: " + (difference / 1000.0) + "s");
               }
            }
         }
         catch (YourCodeSucksException yex)
         {
            // deal with all of our syntax errors, I'm using the console as a convienence
            temp.processScriptErrors(yex);
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
      else
      {
         try
         {
            temp.rppl();
         }
         catch (Exception ex)
         {
            ex.printStackTrace();
         }
      }
   }

   public void consolePrint(String message)
   {
      System.out.print(message);
   }

   public void consolePrintln(Object message)
   {
      System.out.println(message.toString());
   }

   public String consoleReadln()
   {
      try
      {
         return in.readLine();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         return null;
      }
   }
   
   BufferedReader in;

   public TextConsole()
   {
      in = new BufferedReader(new InputStreamReader(System.in));
   }
}
