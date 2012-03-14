#
# ensure that callcc works from a fork...
#

debug(15);

sub func
{
   println("Begin!");
   callcc 
   {
      println("Inside of callcc function");
      return "pHEAR";
   };
   println("Should never happen");
}

global('$handle $value');

$handle = fork(&func);
$value  = wait($handle, 5000);
println($value);
