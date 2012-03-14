#
# test out append
#
@a = @("a", "b", "c");
@b = @(1, 2, 3, 4, 5);

@a = concat(@a, @b, @a);
println(@a);

#
# test that appended lists share cells...
#
@a[2] = "test";
println(@a);

#
# test out head..
#
@b[0] = "blah";

println(@b[0] . " and " . sublist(@b, 1));

println(@b[0] . " and " . @b);

