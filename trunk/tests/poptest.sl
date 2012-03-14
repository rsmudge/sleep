#
# test out the add and push ops in many different scenarios.
#

@a = @("a", "b", "c", "d", "e");
println(@a);

for ($x = 0; $x < 6; $x++)
{
   for ($y = $x; $y < 6; $y++)
   {
      @a = @("a", "b", "c", "d", "e");
      @b = sublist(@a, $x, $y);
      println("sublist(@a, $x $+ , $y $+ ) = " . @b);

      for ($z = 0; $z < ($y - $x); $z++)
      {
         println("   Popped: " . pop(@b));
         println("      " . @a);
         println("      " . @b);
      }
   }
}
