#
# test for scope management...
#

inline bar
{
   pushl($a => $1, $b => $2);
   $z = "pHEAR";
   println($z);
   popl($result => $a * $b);
}

sub foo
{
   local('$z'); 
   $z = "noPHEAR";
   println("This is a test! $z and $result");
   bar(300, 45);
   println("$result and $z");
}

foo();
