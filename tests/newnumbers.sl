#
# test of the new number formats
#

$a = 12345L;
$b =  0xAAAABBBBBBBL;
$c = -0xAAAABBBBBBBL;
$d = 0123L; 

println("$a $b $c | $d");

$a = 12345;
$b =  0xAAABBB;
$c = -0xAAABBB;
$d = -07123777777;

println("$a $b $c | $d");


