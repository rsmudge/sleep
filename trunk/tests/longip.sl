#
# test out converting between dotted quad notation and unsigned integer format for ip
# addresses
#
$ip     = join('.', unpack("B4", pack("I", '3232235777')));
$longip = unpack("I-", pack("B4", split('\.', "192.168.1.1")))[0];

println("$ip and $longip");

$longip1 = unpack("I-", pack("B4", split('\.', "255.255.255.255")))[0];
$ip     = join('.', unpack("B4", pack("I", $longip1)));
$longip2 = unpack("I-", pack("B4", split('\.', $ip)))[0];

println("$ip and $longip1 and $longip2");

$longip1 = unpack("I-", pack("B4", split('\.', "128.129.130.131")))[0];
$ip     = join('.', unpack("B4", pack("I", $longip1)));
$longip2 = unpack("I-", pack("B4", split('\.', $ip)))[0];

println("$ip and $longip1 and $longip2");

$longip1 = unpack("I-", pack("B4", split('\.', "0.0.0.1")))[0];
$ip     = join('.', unpack("B4", pack("I", $longip1)));
$longip2 = unpack("I-", pack("B4", split('\.', $ip)))[0];

println("$ip and $longip1 and $longip2");

$longip1 = unpack("I-", pack("B4", split('\.', "1.0.0.0")))[0];
$ip     = join('.', unpack("B4", pack("I", $longip1)));
$longip2 = unpack("I-", pack("B4", split('\.', $ip)))[0];

println("$ip and $longip1 and $longip2");


