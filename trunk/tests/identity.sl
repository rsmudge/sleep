$g = { println("ggg!"); };
$s = $g . "";

@a = @(1, 2, 3, 4.0, 5, $g);
@b = @(4, 6, 7, 8, 9, $g, $s);

@c = copy(@a);
addAll(@c, @b);
println(@c);

@c = copy(@a);
removeAll(@c, @b);
println(@c);

@c = copy(@a);
retainAll(@c, @b);
println(@c);
