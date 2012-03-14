#
# test out the &sublist function.
#

@array = @("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u");

println(sublist(@array, 10));

# extract a sublist; modifications in the sublist affect the parent
@temp = sublist(@array, 3, 8);
println(@temp);
clear(@temp);

println(@array);

#
@temp = sublist(@array, 4, 6);
push(@temp, "doe");
push(@temp, "ray");
push(@temp, "me");
push(@temp, "fa");

println(@temp);
println(@array);
