#
# test out the add and push ops in many different scenarios.
#

@a = @("a", "b", "c", "d", "e");
println(@a);

for ($x = 0; $x < 6; $x++)
{
   for ($y = $x; $y < 6; $y++)
   {
      for ($z = 0; $z < (($y - $x) + 1); $z++)
      {
         @a = @("a", "b", "c", "d", "e");
         @b = sublist(@a, $x, $y);
         println("sublist(@a, $x $+ , $y $+ ) = " . @b);

         add(@b, "HELLO", $z);
         println("  add at $z $+ :");
         println("     " . @a);
         println("     " . @b);
      }
   }
}
