#
# ensure that byte[] arrays returned by Java functions are converted to sleep byte strings
#

debug(7);

import java.security.MessageDigest;

$data = readb(openf("data/test.jar"), lof("data/test.jar"));

$summer = [MessageDigest getInstance: "MD5"];
[$summer update: $data]; # sleep will now cast a string into a byte array.

$bytes  = [$summer digest];

println("The MD5 value of test.jar is: " . unpack("H*", $bytes)[0]);
