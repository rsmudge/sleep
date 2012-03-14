#
# borderline string manipulation cases...
#

println(substr("ab", 1));
println(substr("a", 1));
println(substr("abc", 2));
println(substr("ab", 2));

println(subarray(@("a", "b"), 1));
println(subarray(@("a"), 1));
println(subarray(@("a", "b", "c"), 2));
println(subarray(@("a", "b"), 2));

println(subarray(@("a", "b", "c"), -1));
println(subarray(@("a", "b", "c"), -2));
println(subarray(@("a", "b", "c"), -3));

println(substr("abc", -1));
println(substr("abc", -2));
println(substr("abc", -3));

@test = @("a", "b", "c", "d");
add(@test, "e", -1);

println(@test);

@test = @("v", "w", "x", "y", "z");
removeAt(@test, -1);

println(@test);

println(subarray(@("a", "b", "c", "d", "e"), 2));
println(substr("abcde", 2));
