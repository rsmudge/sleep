debug(7 | 34);

global('$zz');

sub doit
{
   throw "an exception";
}

sub bar 
{
   local('$s');
   $s = int(doit());
}

sub foo
{
   local('$x');

   try
   {
      $x = bar("aaa", "bbb", "ccc");
   }
   catch $problem 
   { 
   }

   return $x;
}

$zz = foo();
