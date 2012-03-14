#
# test for loops with empty increment/comparison/decrement fields..
# 

for ($x = 0; $x < 5; $x++)
{
   println("Loop 1: $x");
}

for ($x = 0; $x < 5;)
{
   println("Loop 2: $x");
   $x++;
}

$x = 0;
for (; $x < 5; $x++)
{
   println("Loop 3: $x");
}

$x = 0;
for (; $x < 5;)
{
   println("Loop 4: $x");
   $x++;
}

