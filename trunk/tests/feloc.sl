#
#
#

debug(7);

global('$x');

sub foo
{
   foreach $y (@(1, 2, 3, 4, 5, 6, 7))
   {
       # ... :) ...
   }
}

$x = 33;
foo();
println($x);
println($y);
