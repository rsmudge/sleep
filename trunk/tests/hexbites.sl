#
# test some stuff packing and unpacking hex bytes.. dig?!?
#

$string ="000123456789ABCDEF";

# low nybble first
$pack   = pack("h*", $string);

for ($x = 0; $x < strlen($pack); $x++)
{
   println("low -> $[3]x : " . asc(charAt($pack, $x)));
}

($unpack) = unpack("h*", $pack);
println("low: $unpack");

($unpack) = unpack("H*", $pack);
println("...: $unpack");

# high nybble first
$pack   = pack("H*", $string);

for ($x = 0; $x < strlen($pack); $x++)
{
   println("high -> $[3]x : " . asc(charAt($pack, $x)));
}

($unpack) = unpack("H*", $pack);
println("high: $unpack");

($unpack) = unpack("h*", $pack);
println("....: $unpack");

$string ="0001234567890ABCDEF"; # we want to cause an error here...

# low nybble first
$pack   = pack("h*", $string);

println("Packed: $pack");
