#
# a test of import from...

import org.hick.blah.* from: data/test.jar;

$sq = [new SqueezeBox];

for ($x = 0; $x < 10; $x++)
{
   println("Test: " . [$sq squeeze]);
}


