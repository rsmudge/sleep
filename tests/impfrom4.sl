#
# a test of import from... with multiple packages from the same jar file.
#

import org.hick.blah.* from: data/test.jar;
import org.hick.tests.* from: data/test.jar;

$sq = [new SqueezeBox];

for ($x = 0; $x < 10; $x++)
{
   println("Test: " . [$sq squeeze]);
}

$ld = [new TestLoadable];
[$ld scriptUnloaded];


