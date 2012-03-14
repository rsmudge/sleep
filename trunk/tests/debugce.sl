# ensure this is run with -Dsleep.debug=3 passed to the java interpreter.
# just checking to make sure that the check error debugging stuff works..
#

eval("
    println('\\\\'');
");

$handle = openf("fjasjkfajskfjasfjksakjfsjkfjksafjk.txt");

println("I should be doing more stuff here...");
