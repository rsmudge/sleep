#
# test setting of fields...
#

import org.hick.blah.SqueezeBox from: data/test.jar;

$sb1 = [new SqueezeBox];

println([$sb1 squeeze]);
#setField($sb1, sq => 55);
println([$sb1 squeeze]);

println("???: " . [$sb1 sq]);

println("Done?");
