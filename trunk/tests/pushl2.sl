#
# test for scope management...
#

inline bar
{
   pushl();
   pushl();
   popl($result => $a * $b);
}

sub foo
{
   local('$z'); 
   println("Begin");
   bar(300, 45);
   println("End");
}

foo();
