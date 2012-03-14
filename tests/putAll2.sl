@a = @("a", "b", "c");
@b = @(1, 2, 3);

putAll(@a, @b);

println(@a);
@b[1] = "hah!";
println(@a);
println(@b);

