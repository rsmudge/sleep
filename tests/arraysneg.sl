#
# Test some stuff with arrays
#

@test[0] = "Hello";
@test[1] = "Ice cream";
@test[2] = "You";
@test[3] = "Rock";
@test[4] = "my";
@test[5] = "world";

for ($x = 1; $x <= 10; $x++)
{
   $y = $x * -1;
   printf("@test[ $+ $y $+ ]: " . @test[$y]);
}

println("- " x 20);

for ($x = -1; $x >= (0 - size(@test)); $x--)
{
   println("@test[ $+ $x $+ ]: " . @test[$x]);
}
