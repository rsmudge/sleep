@a = @("a", "b", "c", "d", "e", "f", "g", "h");

for ($x = 0; $x < size(@a); $x++)
{
   for ($y = $x; $y < size(@a); $y++)
   {
      println("$x $+ , $y $+ : " . sublist(@a, $x, $y));
   }

   println("$x $+ , all: " . sublist(@a, $x));
}

println(sublist(@a, 4, 2));
