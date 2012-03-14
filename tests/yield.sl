sub simple
{
   println("Test 1");
   yield 1;

   println("Test 2");
   yield 2;

   println("Test 3");
   yield 3;
 
   println("Test 4");
   yield 4;

   println("Test 5");
   yield 5;

   println("Test 6");
   yield 6;

   println("Test 7");
   yield 7;

   println("Done");
   yield 0;
}

$x = simple();
while ($x != 0)
{
   println($x);
   $x = simple();
}

#
#
#

sub range
{
   local('$y');
   $y = 0;
   while ($y < 100)
   {
      yield $y;
      $y++;
   }

   return -1;
}

$x = range();
while ($x != -1)
{
   println("In range? $x");
   $x = range();
}

