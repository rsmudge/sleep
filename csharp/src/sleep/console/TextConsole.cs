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

using  sleep.engine;
using  sleep.parser;
using  sleep.runtime;
using  sleep.error;
using  sleep.taint;

namespace sleep.console{

/** Default implementation of the console proxy class.  Provides a STDIN/STDOUT implementation of the sleep console. */
public class TextConsole : ConsoleProxy
{
   public static void main(String[] args)
   {
      ScriptLoader loader = new ScriptLoader();

      ConsoleImplementation temp = new ConsoleImplementation(null, null, loader);
      temp.setProxy(new TextConsole());

      if (args.Length > 0)
      {
         bool check = false;
         bool ast   = false;
         bool eval  = false;
         bool expr  = false;
         bool prof  = false;
         bool time  = false;
         int     start = 0;

         while (start < args.length && (args[start].startsWith("--") || (args[start].length() >= 2 && args[start].charAt(0) == '-')))
         {
            if (args[start].equals("-version") || args[start].equals("--version") || args[start].equals("-v"))
            {
                java.lang.SystemJ.outJ.println(SleepUtils.SLEEP_VERSION + " (" + SleepUtils.SLEEP_RELEASE + ")");
                return;
            } 
            else if (args[start].equals("-help") || args[start].equals("--help") || args[start].equals("-h"))
            {
                java.lang.SystemJ.outJ.println(SleepUtils.SLEEP_VERSION + " (" + SleepUtils.SLEEP_RELEASE + ")");
                java.lang.SystemJ.outJ.println("Usage: java [properties] -jar sleep.jar [options] [-|file|expression]");
                java.lang.SystemJ.outJ.println("       properties:");
                java.lang.SystemJ.outJ.println("         -Dsleep.assert=<true|false>");
                java.lang.SystemJ.outJ.println("         -Dsleep.classpath=<path to locate 3rd party jars from>");
                java.lang.SystemJ.outJ.println("         -Dsleep.debug=<debug level>");
                java.lang.SystemJ.outJ.println("         -Dsleep.taint=<true|false>");
                java.lang.SystemJ.outJ.println("       options:");
                java.lang.SystemJ.outJ.println("         -a --ast       display the abstract syntax tree of the specified script");
                java.lang.SystemJ.outJ.println("         -c --check     check the syntax of the specified file");
                java.lang.SystemJ.outJ.println("         -e --eval      evaluate a script as specified on command line");
                java.lang.SystemJ.outJ.println("         -h --help      display this help message");
                java.lang.SystemJ.outJ.println("         -p --profile   collect and display runtime profile statistics");
                java.lang.SystemJ.outJ.println("         -t --time      display total script runtime");
                java.lang.SystemJ.outJ.println("         -v --version   display version information");
                java.lang.SystemJ.outJ.println("         -x --expr      evaluate an expression as specified on the command line");
                java.lang.SystemJ.outJ.println("       file:");
                java.lang.SystemJ.outJ.println("         specify a '-' to read script from STDIN");
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
                script = loader.loadScript("STDIN", SystemJ.inJ);
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
               SystemJ.outJ.println(args[start] + " syntax OK");    
            }
            else if (ast)
            {
               SystemJ.outJ.println(script.getRunnableBlock());
            } 
            else
            {
               long beganAt = SystemJ.currentTimeMillis();

               script.runScript();                                     // run the script...

               if (prof)
               {
                  script.printProfileStatistics(SystemJ.outJ);
               }

               if (time)
               {
                   long difference = SystemJ.currentTimeMillis() - beganAt;
                   SystemJ.outJ.println("time: " + (difference / 1000.0) + "s");
               }
            }
         }
         catch (YourCodeSucksException yex)
         {
            // deal with all of our syntax errors, I'm using the console as a convienence
            temp.processScriptErrors(yex);
         }
         catch (java.lang.Exception ex)
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
         catch (java.lang.Exception ex)
         {
            ex.printStackTrace();
         }
      }
   }

   public void consolePrint(String message)
   {
      SystemJ.outJ.print(message);
   }

   public void consolePrintln(Object message)
   {
      SystemJ.outJ.println(message.toString());
   }

   public String consoleReadln()
   {
      try
      {
         return inJ.readLine();
      }
      catch (java.lang.Exception ex)
      {
         ex.printStackTrace();
         return null;
      }
   }
   
   java.io.BufferedReader inJ;

   public TextConsole()
   {
      inJ = new java.io.BufferedReader(new java.io.InputStreamReader(SystemJ.inJ));
   }
}
}