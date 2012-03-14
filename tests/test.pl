#!/usr/bin/perl
# Regression test perl script.  A quick way to make sure my crazy changes aren't
# breaking my precious language.
#
# Runs each sleep script in this directory, compares the results to the file output
# w/i the output directory.
#
# If they match - congratulations the script worked okay.
#
# If they don't match - something is broken.
#
# Executing the regression test:
# (from within the examples directory type)
#
# [user ~/sleep/tests]$ perl test.pl
#

@files = `ls -1 *.sl`;
chomp(@files);

if (!-e "output")
{
   `mkdir output`;
}

foreach $var (@files)
{
   $ARGS = "";
   $PROPS = "";
   if ($var eq "debugce.sl") { $PROPS = "-Dsleep.debug=3"; }
   if (substr($var, 0, 5) eq "taint") { $PROPS = "-Dsleep.taint=true"; $ARGS = "\"2 + 2\""; }

   if (!-e "./output/$var")
   {
      `java $PROPS -jar ../sleep.jar ./$var >./output/$var $ARGS`;
      push @errors, "$var output does not exist, creating it";
   }
   else
   {
      $expected_value = join("", `cat ./output/$var`);

      $script_value   = join("", `java $PROPS -jar ../sleep.jar ./$var $ARGS`);

      if ($expected_value ne $script_value)
      {
         push @errors, "Output of $var does not match expected output.";

         if ($ARGV[0] eq "-dump")
         {
            print "\njava $PROPS -jar ../sleep.jar ./$var\n";
            print "\n".$script_value."\n";
         }

         print "$var -- broken!\n";
      }  
      else
      {
         print "$var \n";
      }
   }
}

if ($errors[0] eq "")
{
   print "\n[31337] All tests passed.  Looks like nothing is broken.\n";
}
else
{
   print "\n";
}

foreach $var (@errors)
{
   print "[WARNING] " . $var . "\n";
}

