@a = @("a", "b", "c", "d", "e", "f", "g", "h");

for ($x = 0; $x < size(@a); $x++)
{
   println("$x $+ : " . @a[$x]);
}

for ($x = 1; $x < 9; $x++)
{
   $y = $x * -1;
   println("$y $+ : " . @a[$y]);
}

println(@a[345]);
