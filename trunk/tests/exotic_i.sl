#
# Test of exotic array indexing :)
#

#
# array indices on function calls...
#
println("This is a test: " . array(1, 2, 3, 4)[2]);
println("This is a test: " . array(1, array("a", "b", "c"), 3, 4)[1][1]);
println("This is a test: " . array(1, array("a", "b", array("!", 3, "@")), 3, 4)[1][2][2]);

#
# array indices on expressions (eh?!?@)
#

@temp = array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

println("This is a test: " . (@temp)[3]);

#
# array indices on scalars
#

$blah = @temp;

println("This is a test: " . $blah[3]);

