debug(debug() | 128);

$list = [new LinkedList];
[$list add: "puppies!!"];
[$list add: shift(@ARGV)];

println($list);

$item = [$list get: 1];
untaint($item);
println(expr($item));

$value = taint("3 * 9");

if (-istainted $value)
{
   println("$value is tainted (and so is this)");
}

println(expr($value));

