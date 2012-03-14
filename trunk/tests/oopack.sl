global('$str $num $func $x');

debug(7);

#
# test of pack/unpack with sleep objects
#

$x = pack("o3", "this is a test", 3, { println("doit!"); });

# println(strlen($x));

($str, $num, $func) = unpack("o*", $x);

[$func];

if (-isnumber $num) { println("It is a number: $num"); }

println($str);
