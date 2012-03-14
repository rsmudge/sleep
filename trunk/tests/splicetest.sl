#
# test out list splicing on sublists
#

@a = @("a", "b", "c", "d", "e", "f");
println(@a);

for ($x = 0; $x < 7; $x++)
{
   for ($y = $x + 1; $y < 7; $y++)
   {
      @a = @("a", "b", "c", "d", "e", "f");
      @b = sublist(@a, $x, $y);
      println(@b);

      splice(@b, @(1, 2, 3), 1, 2);
      println("  " . @b);
      println("  " . @a);
   }
}
