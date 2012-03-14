#
# ...
#

$x = [SleepUtils getScalar: "hi dudez"];
println(typeOf($x));
println([$x getClass]);

$y = [SleepUtils getHashScalar];
println(typeOf($y));
$y["pHEAR"] = "blah bleh bleh";
println($y);
