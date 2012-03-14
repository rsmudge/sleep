#
# test a sublist of a sublist of a sublist changes..
#

@a = @("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
@b = subarray(@a, 2, 8);
@c = subarray(@b, 2, 4);

println(@a);
println(@b);
println(@c);

@c[2] = "pHEAR";
println(@a);
println(@c);
println(@b);
