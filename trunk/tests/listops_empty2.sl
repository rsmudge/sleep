#
# operations on empty sublists.
#

@a = @("a", "b", "c", "d", "e", "f", "g", "h");
println(@a);

@b = sublist(@a, 2, 3);
println(@b);
add(@b, "PHEAR");
println(@a);
println(@b);

clear(@b);
println(@a);
println(@b);

add(@b, "uNF");
println(@a);
println(@b);
