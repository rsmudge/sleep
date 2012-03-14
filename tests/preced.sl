#
#
#

# demonstrating a test of a bug fix.
($x, $y, $z) = %(key => "value");

$x["3"] = "blah";
println("$x and $y and $z");

# show off assignment op on simple scalars
$x = 3;
$x += 10;
$x -= 4;
println($x);

# assignment op with tuples... ooh...

($x, $y, $z) = 5;
println("\$x = $x $+ ; \$y = $y $+ ; \$z = $z");

($x, $y, $z) *= 2;
println("\$x = $x $+ ; \$y = $y $+ ; \$z = $z");

($x, $y, $z) -= @(3, 2, 1);
println("\$x = $x $+ ; \$y = $y $+ ; \$z = $z");

($x, $y, $z) <<= 2;
println("\$x = $x $+ ; \$y = $y $+ ; \$z = $z");

($x, $y, $z) = @(7, 8, 9);
($x, $y, $z) &= 5;
println("&= result: \$x = $x $+ ; \$y = $y $+ ; \$z = $z");

@a = @(10, 12, 14);

# special case... one element..
(@a) *= @(5, 4, 3);
println(@a);

# normal assignment works different than op assignment... basically this will
# assign the first element of the anonymous array to @a (the element happens to be
# an array itself).  
(@a) = @(@("this is a test"));
println(@a);

#
# append 3, 4, and 5 to the first, second, and third elements of @a.
#
(@a) .= @(3, 4, 5);
println(@a);

# normal behavior
(@a, @b, @c) = @(@("a", "b", "c"), @("d", "e", "f"), @("g", "h", "i"));
println("@a: " . @a);
println("@b: " . @b);
println("@c: " . @c);

