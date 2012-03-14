#
# test of the Sleep random number generator.
#

srand(12345);

@a = @(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

for ($x = 0; $x < 6; $x++)
{
   println(rand(@a));
}

println(rand());
println(rand());
println(rand());
println(rand());

