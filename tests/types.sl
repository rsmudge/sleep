#
# Test of conversions from Java types to Sleep types and vice versa...
#

$int   = [Integer valueOf: "3"];
$long  = [Long    valueOf: "47823784783244743"];
$boolT = ["a" equals: "a"];
$boolF = ["a" equals: "b"];
$obj   = [new java.util.StringTokenizer: "Hello"];

println("Int    value is: $int   - " . [$int getClass]);
println("Long   value is: $long  - " . [$long getClass]);
println("True   value is: $boolT - " . [$boolT getClass]);
println("False  value is: $boolF - " . [$boolF getClass]);
println("Object value is: <tokenizer> " . [$obj getClass]);

