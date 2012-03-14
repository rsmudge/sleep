#
# reproduce a deadlock problem with fork...
#
$REAL = 453947233; # output of UNIX crc32 command for test.jar

# third test.. the kewlest test of them all...

$sumfork = fork({
   $summer = checksum($source);
   readAll($source);
   return checksum($summer);
});

writeb($sumfork, readb(openf("data/test.jar"), lof("data/test.jar")));
closef($sumfork);

println("Checksum of written data is: " . formatNumber(wait($sumfork), 10, 16));

