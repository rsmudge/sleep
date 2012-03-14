#
# test of new messages...
#

eval('
println($null, "this is a test");
');
#
#


@array = @("a", "b", "c", "d");
add(@array, "this is another test", -10);
println(@array);

#
#
