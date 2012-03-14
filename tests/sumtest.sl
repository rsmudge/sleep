#
# test out different forms of the sum function
#

@test = @(1, 2, 4, 8, 16, 32, 64, 128);

# simple, sum an array
println(sum(@test));

# sum an array multiplied by a constant
println(sum(@test, { return 2; }));

# sum multiple array products
@a = @(1, 2, 3, 4, 5);
@b = @(2, 3, 4, 5, 6);
@c = @(3, 4, 5, 6, 7);

println(sum(@a, @b, @c)); 

# should be $null or 0.0
println(sum());
println(sum(@()));

# ...
sub to100
{
   local('$x');
   for ($x = 0; $x < 100; $x += 3)
   {
       yield $x;
   }
}

println(sum(&to100, { return 2; }));

# 0 out the later args.
@test2 = @(1, 1, 1, 1);
println(sum(@test, @test2));
