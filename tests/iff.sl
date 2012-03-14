#
# Testing beloved iff featured
#

$x = 10;
println("Took " . $x . " second" . iff($x > 1, "s", ""));

$x = 1;
println("Took " . $x . " second" . iff($x > 1, "s", ""));
