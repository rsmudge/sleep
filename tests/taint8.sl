debug(debug() | 128);

$list = [new LinkedList];
[$list add: "puppies!!"];
[$list add: shift(@ARGV)];

println($list);

#

$item = [$list get: 1];
println(expr($item));
