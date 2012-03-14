#
# test to make sure returns work correctly in different loops...
#

sub test1
{
   $x = 0;
   while ($x < 100)
   {
      println("$x");
      if ($x == 10)
      {
         println("x is 10... returning...");
         return $x;
      }
      $x++;
   }

   println("Should never get here");
}

sub test2
{
   for($x = 0; $x < 100; $x++)
   {
      println("$x");
      if ($x == 6)
      {
         println("x is 6... returning...");
         return $x;
      }
   }

   println("Should never get here");
}

sub test3
{
   @temp = array('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i');
   foreach $var (@temp)
   {
      println("$var");
      if ($var eq "c")
      {
         println("var is c... returning...");
         return $var;
      }
   }

   println("Should never get here");
}

println(test1());
println(test2());
println(test3());
