$data = pack('CidZl', "A", 42, 3.5, "hehe this is a string", 1234567890);

($char, $int, $double, $string, $long) = unpack('CidZl', $data);

println("char   data is: $char");
println("int    data is: $int");
println("double data is: $double");
println("string data is: $string");
println("long   data is: $long");

println("-" x 40);

#
# lets try this..
#

# pack everything in little endian order...
$data = pack('C- i- d- Z- l-', "A", 2, 3.5, "hehe this is a string", 1234567890);

# unpack everything in big endian order (default)
($char, $int, $double, $string, $long) = unpack('C+ i+ d+ Z+ l+', $data);

println("char   data is: $char");
println("int    data is: $int");
println("double data is: $double");
println("string data is: $string");
println("long   data is: $long");

println("NORMAL BYTE: " . [Integer toBinaryString: 2]);
println("LITTLE BYTE: " . [Integer toBinaryString: $int]);

println("-" x 40);

# pack everything in little endian order...
$data = pack('C- i- d- Z- l-', "A", 42, 3.5, "hehe this is a string", 1234567890);

# unpack everything in little endian order
($char, $int, $double, $string, $long) = unpack('C- i- d- Z- l-', $data);

println("char   data is: $char");
println("int    data is: $int");
println("double data is: $double");
println("string data is: $string");
println("long   data is: $long");

println("-" x 40);

#
# other tests..
# 
$data = pack('b3', 32, -3, 4);

($u1, $u2, $u3) = unpack('B3', $data);
($s1, $s2, $s3) = unpack('b3', $data);

println("Byte Unsigned: $u1 $u2 $u3");
println("Byte Signed  : $s1 $s2 $s3");

println("-" x 40);

$data = pack('s3', -25, -35, 16000);

($u1, $u2, $u3) = unpack('S3', $data);
($s1, $s2, $s3) = unpack('s3', $data);

println("Short Unsigned: $u1 $u2 $u3");
println("Short Signed  : $s1 $s2 $s3");

println("-" x 40);


#
# Arch test..
#

$temp = iff(unpack('i-', pack('i+', 1))[0] == 1, "big endian", "little endian"); # use ! instead of - for a real test (! is native endianess, - is always little, + is always big)
println("Test: $temp");

println("-" x 40);

#
# I hate these stupid UTF strings... blah
#

$data = pack('UUU', "this string is lame as hell", "this string is also lame", "this string is even lamer");
($s1, $s2, $s3) = unpack('UUU', $data);

println("1: $s1");
println("2: $s2");
println("3: $s3");

