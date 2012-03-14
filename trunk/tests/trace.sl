#
# test out the function call traceing stuff...
#
debug(15);

[[System out] println: "this is a test" ];
[[System out] println: [Math pow: 3, 4]];
println([Math pow: 3, 5]);

[[java.lang.System out] println: "testing again..."];

$list = [new LinkedList: [sleep.runtime.SleepUtils getListFromArray: @("a", "b", "c")]];

println($list);
