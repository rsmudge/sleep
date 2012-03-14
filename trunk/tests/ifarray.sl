#
# test array indices within predicates
#

@temp  = array("aaa", "bbb", "ccc", array("dddd", "eeee", 1, 2, 3));
$value = "this is a string";

println("Case 1: " . iff(@temp[1] eq "bbb", "true", "false"));
println("Case 2: " . iff(@temp[3][0] eq "dddd", "true", "false"));
println("Case 3: " . iff(@temp[3][1] eq "dddd", "true", "false")); # false


println("Case 4: " . iff("dddd" eq @temp[3][0], "true", "false"));
println("Case 5: " . iff("dddd" eq @temp[3][1], "true", "false")); # false

println("Case 6: " . iff(split(' ', $value)[3] eq "string", "true", "false"));
println("Case 7: " . iff("string" eq split(' ', $value)[3], "true", "false"));

println("Case 8: " . iff(split(' ', $value)[3] eq "string" && @temp[1] eq "bbb" && @temp[3][0] eq "dddd", "true", "false"));


println("Case 9: " . iff(-isnumber @temp[3][4], "true", "false"));
println("Case a: " . iff(-isnumber @temp[3][4] && -isnumber @temp[3][3], "true", "false"));
println("Case b: " . iff(-isnumber @temp[3][4] && !-isnumber @temp[3][0], "true", "false"));

println("Case c: " . iff(-isnumber [Math pow: 3.0, 4.0], "true", "false"));
println("Case d: " . iff(-isnumber [Math pow: 3.0, 4.0] && @temp[3][4] == 3, "true", "false"));
println("Case e: " . iff(-isnumber [Math pow: 3.0, 4.0] && @temp[3][4] != 3, "true", "false")); # false

#
# quick -isnumber test since we're here :)
#
println("Test w: " . iff(-isnumber 3.0, "true", "false"));
println("Test x: " . iff(-isnumber "3...0", "true", "false"));  # false
println("Test y: " . iff(-isnumber "30.", "true", "false"));    # false

#
# Other tests...
#
@array = array(3, 4, 5, 6, 7, 8);

println("Testing: " . (@array[0] * 4));

