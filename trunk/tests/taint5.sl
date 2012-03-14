debug(debug() | 128);

$x = @ARGV[0];
println("$x is tainted?");

untaint($x);
println("$x is now untainted :)");
