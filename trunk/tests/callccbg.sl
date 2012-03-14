#
# potential callcc bug...
#

sub bar
{
   println("Hello from bar!");
}

sub foo
{
   local('$x');
   $x = 100;
   if ($x == 100)
   {
      if ($x != 10)
      {
         callcc &bar;
         println("Post cc same block");
      }
   }

   println("Ho! post continuation!");
}

foo();
