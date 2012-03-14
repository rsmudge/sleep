#
# Test some stuff with arrays
#

@test[0] = "Hello";
@test[1] = "Ice cream";
@test[2] = "You";
@test[3] = "Rock";
@test[4] = "my";
@test[5] = "world";

for ($x = 0; $x < size(@test); $x++)
{
   printf("@test[ $+ $x $+ ]: " . @test[$x]);
}
