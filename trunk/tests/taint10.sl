#
# invasion of the wrappers :)
#

debug(7 | 128);

# array fun
global('$list $ds $var');

$list = [new LinkedList];
map(let({ [$list add: $1]; }, \$list), @("a", "b", "c", "d", "e"));

$ds = [SleepUtils getArrayWrapper: $list];
println("before expr: $ds[0]");
taint($ds);
println("after  expr: $ds[0]");

foreach $var ($ds)
{
   println("... $var :)");
}

# hash fun
global('$key $map');

$map = [new HashMap];
[$map put: "a", "apple"];
[$map put: "b", "barracuda"];
[$map put: "c", "cat"];

$ds = [SleepUtils getHashWrapper: $map];
println("Before: " . $ds["a"]);
taint($ds);
println("After: " . $ds["c"]);

foreach $key => $var ($ds)
{
   println(">>> $key - should be ok");
   println("... $var :)");
}
