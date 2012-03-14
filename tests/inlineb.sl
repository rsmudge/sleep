#
# test a broken inline call.
#

inline foo
{
   $x += 3;
   yield $x;
}

sub bar
{
   local('$x');
   $x = 2;
   $z = 10 * foo(); # this is a bug and it is bad.
   println($z);
   return 10;
}

println(bar());
println(bar());
