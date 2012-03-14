#
# test the digest API
#

$REAL = '052168a92bfc545941d9352b35288ac3';# output of UNIX md5 command for test.jar

# first test... digest an I/O handle :)

$handle = openf("data/test.jar");
$summer = digest($handle);
readAll($handle); 
$bytes  = digest($summer);

$sum    = unpack("H*", $bytes)[0];

println("(1) Digest value of test.jar is: " . $sum . " good? " . iff($sum eq $REAL, "yes", "no"));
closef($handle);

println("(2) Another test of repacked data: " . iff(pack("H", $sum) eq $bytes, "good", "broken"));

# second test... checksumming raw data

$handle = openf("data/test.jar");
$data   = readb($handle, lof("data/test.jar")); # read in the entire file
$sum    = unpack("H*", digest($data, "MD5"))[0];
println("(3) Digest value of test.jar is: " . $sum . " good? " . iff($sum eq $REAL, "yes", "no"));
closef($handle);

# third test.. the kewlest test of them all...

$sumfork = fork({
   readb($source, lof("data/test.jar"));
});

$summer = digest($sumfork, ">MD5");
writeb($sumfork, $data);
closef($sumfork);
wait($sumfork);
println("Digest of written data is: " . unpack("H*", digest($summer))[0]);

