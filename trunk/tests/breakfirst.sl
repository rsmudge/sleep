#
# another test of break to make sure it breaks right away...
#

println("Blah...");

for ($x = 0; $x < 100; $x++)
{
   if ($x == 0)
   {
      break;
   }
 
   echo("$x");
}
