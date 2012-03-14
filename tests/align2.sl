#
# ensure aligning works with some special cases..
#

sub f
{
   return 10;
}

$x = "test";

println("$[f()]x $+ |");
println("$[@(10, @(10, -10))[1][1]]x $+ |");

