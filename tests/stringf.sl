#
# test some of the string functions
#

println("Test 1: " . replaceAt("this is a test", "", 4, 3));
println("Test 2: " . replaceAt("this is a test", "function ", 10, 0));

println("Test 3: " . mid("this is a test", 5, 2));
println("Test 4: " . mid("this is a test", 5));
println("Test 5: " . substr("this is a test", 8));
println("Test 6: " . substr("this is a test", 10, 12));
     #                       01234567890123
