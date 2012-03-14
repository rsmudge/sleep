#
# no news is good news with this script...
#

debug(7);

global('$handle $in $x');

$handle = openf("../sleep.jar");

$in = readb($handle, lof("../sleep.jar"));
assert strlen($in) == lof("../sleep.jar");

for ($x = 0; $x < strlen($in); $x++)
{
   $in = byteAt($in, $x);
   assert $in > 0 && $in < 256;
}

println("Done!");
