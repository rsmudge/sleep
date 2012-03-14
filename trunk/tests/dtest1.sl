#
# test out strict debug mode...
#

global('$a');

debug(7); # turn on all debugging options (4 is strict)

$a = "hello";
$b = "world";

println("$a $b");

debug(0);

$c = "hi";

println("$c $b");
