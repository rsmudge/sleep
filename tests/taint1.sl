debug(debug() | 128);

#detaint(@ARGV[0]);

$z = @ARGV[0];
$x = $z . "pHEAR";
$y = 34;

sub foo
{
   $string = $1 . $2;
   println("Love it: $string");
   return 99;
}

$v = foo($y, " <-- uNF");
$u = foo($z, " <-- BLAH");
$t = foo($x, $z);
