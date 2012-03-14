#
# operations on empty sublists.
#

@a = @("a", "b", "c", "d", "e", "f", "g", "h");
println(@a);

@b = sublist(@a, 2, 3);
println(@b);
push(@b, "PHEAR");
println(@a);
println(@b);

clear(@b);
println(@a);
println(@b);

push(@b, "uNF");
println(@a);
println(@b);
