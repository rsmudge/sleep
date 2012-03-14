#
# a test of import from... (testing full path import, eh?!?)

import org.hick.blah.SqueezeBox from: data/test.jar;

$sq = [new SqueezeBox];

for ($x = 0; $x < 10; $x++)
{
   println("Test: " . [$sq squeeze]);
}


