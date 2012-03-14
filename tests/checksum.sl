#
# test the checksum API
#
$REAL = 453947233; # output of UNIX crc32 command for test.jar

# first test... checksumming an I/O handle :)

$handle = openf("data/test.jar");
$summer = checksum($handle);
readAll($handle); # read in the entire contents but we'll discard it...
$sum    = checksum($summer);
println("(1) Checksum value of test.jar is: " . $sum . " good? " . iff($sum eq $REAL, "yes", "no"));
closef($handle);

# second test... checksumming raw data

$handle = openf("data/test.jar");
$data   = readb($handle, lof("data/test.jar")); # read in the entire file
$sum    = checksum($data, "CRC32");
println("(2) Checksum value of test.jar is: " . $sum . " good? " . iff($sum eq $REAL, "yes", "no"));
closef($handle);

# third test.. the kewlest test of them all...

$sumfork = fork({
   readb($source, lof("data/test.jar"));
});

$summer = checksum($sumfork, ">CRC32");
writeb($sumfork, $data);
closef($sumfork);
wait($sumfork);
println("Checksum of written data is: " . checksum($summer));

