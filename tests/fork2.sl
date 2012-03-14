#
# more scope testing of fork... oi vey.
#

#
# easy test
# 
sub function
{
   println("\$x is $x from &function");
   $x++;
} 

$a = lambda(&function, $x => 1);

$handle = fork({
   [$func];
}, $func => $a);

wait($handle);

[$a];
[$a];

$handle = fork({
   [$func];
}, $func => $a);

wait($handle);

#
# yield out of a coroutine and resume in a fork... ooh..
#

sub coroutine
{
   local('$z');
   println("Coroutine start");
   $z = 33;

   yield "checkpoint A";

   println("After checkpoint a and \$z is $z");
   $z++;

   yield "checkpoint B";

   println("After checkpoint b and \$z is $z");
   
   return "done: $z"; # reset the whole damned thing..
}

println("Return value in main is: " . coroutine());

$handle = fork({
   println("Return value within fork is: " . [$_coroutine]);
}, $_coroutine => &coroutine);

wait($handle);

println("Return value in main is: " . coroutine());

