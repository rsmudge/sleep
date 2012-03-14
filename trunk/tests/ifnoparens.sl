#
# Testing out various if statements with no parentheses
#

$x     = 3;
@array = array("a", "b", "c");

if ($x < size(@array) + 3)
{
   println("Check 1");
}

if ($x < size(@array) + 3 && size(@array) == 3)
{
   println("Check 2");
}

if ($x < size(@array) + 3 && size(@array) != 3)
{
   println("Should not happen 3");
}

if ($x < size(@array) + 3 && size(@array) == ((3 + 4 - 4) * 9) / 9)
{
   println("Check 4");
}

if ($x < size(@array) + 3 && size(@array) != 9 % 6)
{
   println("Should not happen 5");
}

if ($x < size(@array) + 3 && size(@array) == 9 % 6)
{
   println("Check 6");
}

if ($x < size(@array) + 3 && size(@array) == ((3 + 4 - 4) * 9) / 9 && $x == 3)
{
   println("Check 7");
}

println(iff($x < size(@array) + 3 && size(@array) == ((3 + 4 - 4) * 9) / 9 && $x == 3, "Check 8", "Broke 8"));

