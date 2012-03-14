#
# test of the new watch option...
# 
global('$x $y');

$x = 3;

watch('$x $y');

sub foo
{
   $1 = $1 * 5;
   return $1;
}

$y = foo($x);
println($y);
$y = "this is a string";
