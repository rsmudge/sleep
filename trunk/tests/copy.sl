#
# copying a hash...
#

%hash = %(a => "apple", b => "bananna", g => "grape fruit", k => "kiwi");
%h2   = copy(%hash);

println(%h2);
println(keys(%h2));
