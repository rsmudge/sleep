#
# a bug with some ambiguity between hash literals and the MOD operator
#

%hash = %(a => "apple", b => "bats", c => "cats");
$expr = 9 % (3 + 4);

println("Hash is: " . %hash);
println("Expression is: " . $expr);

