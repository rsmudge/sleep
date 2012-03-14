#
# Test the work done on the concatenation operator...
#

println("This is a test: ".(4 * 12));

$name = "Raffi";

println("My name is: " . $name);
println("My name is: ".$name);

println("Lots of concat: "."a".'b'.'c'.1.2.3.4.5.6);
