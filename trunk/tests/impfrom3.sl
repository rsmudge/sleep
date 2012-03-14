#
# test of the error checking with import from i.e. reporting that the file was not found.
#

import org.hick.blah.SqueezeBox from: data/tes.jar;

$sq = [new SqueezeBox];

for ($x = 0; $x < 10; $x++)
{
   println("Test: " . [$sq squeeze]);
}


