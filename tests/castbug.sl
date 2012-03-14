#

$x = cast(@(), "o");
println([$x getClass]);

$x = cast(@(), "o", 3, 0);
println([$x getClass]);

$x = cast(@(), "o", 3, 4, 5, 6, 7, 8, 0);
println([$x getClass]);

