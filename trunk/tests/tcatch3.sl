debug(15 | 34);

sub recurse
{
   local('$x');
   $x = [Integer parseInt: $1];
   if ($x == 0)
   {
      return recurse("shlfjkalsf");
   }
   return $x * recurse($x - 1);
}

try
{
   println(recurse(4));
}
catch $ex
{
   println("didn't work: $ex");
   printAll(getStackTrace());
}
