#
# Regression test Sleep script.  A quick way to make sure my crazy changes aren't
# breaking my precious language.
#
# Runs each sleep script in the tests/ directory, compares the results to the file with the
# same name w/i the tests/output directory.
#
# If they match - congratulations the script worked okay.
#
# If they don't match - something is broken.
#
# Executing the regression test:
# (from within the top level Sleep directory type)
#
# [user ~/sleep/]$ java -jar sleep.jar runtests.sl
#
# Advantages over the original test.pl Perl script
# ----------
# 1) most scripts are executed within one Java instance avoiding costly process creation overhead
#   translation: much faster
# 2) script output is sanitized for file/directory paths and object addresses, things that change
#    compile to compile and system to system.

debug(7 | 34);

import sleep.error.RuntimeWarningWatcher;
import sleep.error.YourCodeSucksException;
import sleep.runtime.ScriptLoader;
import sleep.bridges.io.IOObject; 
import sleep.parser.ParserConfig;

sub runScript
{
   local('@args $value $handle $read $exception');
   @args = split(' ', $1);
   push(@args, "2 + 2");

   try
   {
      $handle = exec(@args, $null, cwd());
      $value = readb($handle, -1);

      closef($handle);
   }
   catch $exception
   {
      println("&runScript: " . @_ . " - $exception");
   }

   return $value;
}

sub executeScript
{
   local('$loader $script $buffer $error $watcher');

   $buffer = allocate();

   # this is a hack to get around Sleep's synchronization model with the runtime warning watcher.
   # an isolated (i.e. not used anywhere else) script environment is created by fork, within it we generate an anonymous
   # function proxying the Java object we wish to emulate.  We then return that Java object.  When a method is called on
   # this object it will not block for the test script running in the main thread (this was causing some fork test cases
   # to freeze).

   $watcher = wait(fork({ return newInstance(^RuntimeWarningWatcher, lambda({ println($buffer, $1); }, \$buffer)); }, \$buffer));

   try
   {
      $loader = [new ScriptLoader];

      [ParserConfig setSleepClasspath: cwd()];

      $script = [$loader loadScript: getFileProper($1)];
      [$script chdir: cwd()];
      [$script addWarningWatcher: $watcher];
   }
   catch $error
   {
      return [$error formatErrors];
   }
 
   [IOObject setConsole: [$script getScriptEnvironment], $buffer];

   [$script runScript];

   closef($buffer);
 
   return readb($buffer, available($buffer));
}

#
# fix stuff that is ambiguous between tests i.e. file paths, addresses in object string representations, and closure numbers
#
sub sanitize
{
   $1 = replace($1, '(\&closure\[.*?\]\#)\d+', '$1X');
   $1 = replace($1, '/Users/raffi/sleepdev/sleep', '==CWD==');
   $1 = replace($1, getFileParent(cwd()), '==CWD==');
   $1 = replace($1, '([\.\[][a-zA-Z_0-9;]+@)[0-9a-f]{4,6}', '$1######');
   $1 = replace($1, '(asc@)[0-9a-f]{4,6}', '$1######');
   $1 = replace($1, '\$Proxy\d+?', '\$Proxy#');
   return $1;
}

sub runTests
{
   local('$script $value @scripts @errors $handle $compare %special $x $read');

   chdir("tests");

   # some special scripts that require command line execution to test out some stuff...
   for ($x = 0; $x < 12; $x++)
   {
      %special["taint $+ $x $+ .sl"]  = "java -Dsleep.taint=true -jar ../sleep.jar taint $+ $x $+ .sl";
   }
   %special["debugce.sl"] = "java -Dsleep.debug=3 -jar ../sleep.jar debugce.sl"; 
   %special["tcatchex.sl"] = "java -jar ../sleep.jar tcatchex.sl";     # calls System.out
   %special["odd.sl"]      = "java -jar ../sleep.jar odd.sl";          # calls System.out  
   %special["objects.sl"]  = "java -jar ../sleep.jar objects.sl";      # calls System.out
   %special["trace.sl"]    = "java -jar ../sleep.jar trace.sl";        # calls System.out
   %special["wrong.sl"]    = "java -jar ../sleep.jar wrong.sl";        # calls System.out
   %special["cmdline.sl"]  = "java -jar ../sleep.jar cmdline.sl";      # evaluates $__SCRIPT__ created by TextConsole.main
   %special["multih.sl"]   = "java -jar ../sleep.jar multih.sl";       # calls System.out
   %special["use.sl"]      = "java -jar ../sleep.jar use.sl";          # calls System.out
   %special["use2.sl"]     = "java -jar ../sleep.jar use2.sl";         # calls System.out
   %special["convertds2.sl"] = "java -jar ../sleep.jar convertds2.sl"; # calls System.out
   %special["convertds3.sl"] = "java -jar ../sleep.jar convertds3.sl"; # calls System.out
   %special["convertds4.sl"] = "java -jar ../sleep.jar convertds4.sl"; # calls System.out
   %special["setfield.sl"]    = "java -jar ../sleep.jar setfield.sl";    # calls System.out
   %special["setfield3.sl"]   = "java -jar ../sleep.jar setfield3.sl";   # calls System.out
   %special["process.sl"]    = "java -jar ../sleep.jar process.sl";    # instantiates ScriptLoader which is immune to current working directory.

   @scripts = filter({ return iff("*.sl" iswm $1, getFileName($1)); }, ls());
   foreach $script (@scripts)
   {
      print("$[25]script");

      if (%special[$script] is $null && -exists "output/ $+ $script")
      {
         print("  . ");
         $value = executeScript($script);
      }
      else
      {
         print("  X ");
         $value = runScript(iff(%special[$script] is $null, "java -jar ../sleep.jar $script", %special[$script]));
      }

      if (!-exists "output/ $+ $script")
      {
         $handle = openf(">output/ $+ $script");
         writeb($handle, $value);
         closef($handle);

         println(" - did not exist, creating output");
      }
      else 
      { 
         $handle = openf("output/ $+ $script");
         $compare = readb($handle, -1);
         closef($handle);

         sanitize($compare);
         sanitize($value);

         if ($compare eq $value)
         {
            println();
         }
         else
         {
            println(" - did not match expected output");
            push(@errors, "$script - did not match expected output");
  
            $handle = openf(">../ $+ $script $+ .broken");
            writeb($handle, $value);
            closef($handle);

            $handle = openf(">../ $+ $script $+ .compare");
            writeb($handle, $compare);
            closef($handle);
         }
      }
   }

   if (size(@errors) == 0)
   {
      println("*** All tests passed! :) ***");
   }
   else
   {
      println("*** ERRORS Detected ***");
      printAll(@errors);
   }
}

runTests();
