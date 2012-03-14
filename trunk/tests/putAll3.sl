@a = @("v1", "v2", "v3");
@b = @("k1", "k2", "k3");

putAll(%hash, @b, @a);

println(%hash);
%hash["k2"] = "test";
println(%hash);
println(@a);
println(@b);

