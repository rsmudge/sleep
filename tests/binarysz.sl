#
# test of some pack/unpack fringe cases...
#

# looking for the following
# when we write a Z10 string and we happen to hit the 10 char length, no \0 should be written
# I just want to make sure said string is read in correctly as well...
#

$temp = pack('Z10 I', "0123456789", 33);

println("Strlen: " . strlen($temp)); # should be 14

($string, $int) = unpack('Z10 I', $temp);

println("Test: $string and $int");

$temp2 = pack('Z10 I', "abcde", 45);

println("Strlen: " . strlen($temp2)); # should be 14

($string2, $int2) = unpack('Z10 I', $temp2);

println("Test: $string2 and $int2");

