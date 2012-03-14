#
# Testing beloved iff featured (these forms used to be the cause of an error, as of Sleep 2.1-b21 they are good)
#

$x = 10;
println("Took " . $x . " second" . iff($x > 1, "s"));

$x = 1;
println("Took " . $x . " second" . iff($x > 1, "s", ""));

println("Last test? " . iff($x == 1));
println("Last test? " . iff($x != 1));

