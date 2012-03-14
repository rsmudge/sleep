#
# test some of the functional programming style functions...
#

@array = array("a", "b", "c", "d", "e", "f", "g", "h", "i");

sub func1
{
   if ($1 eq "a" || $1 eq "c")
   {
      return $null;
   }
   else
   {
      return "letter: $1";
   }
}

println("Test 1");
@a = map(&func1, @array);
printAll(@a);

println("Test 2");
@b = filter(&func1, @array);
printAll(@b);

println("Test 3");
$val = search(@array, { if ($1 eq "c") { return "found c! $1 at $2"; } });
println("Check: $val");

println("Test 4");
$val = search(@array, { if ($1 eq "c") { return "found c! $1 at $2"; } }, 4);
println("Check: $val");

println("Test 5");
push(@array, "test");
add(@array, "test 2", 3);
printAll(@array);

println("Test 6");
@c = array(1, 2, 3, 4, 5);
@d = array(1, 2, 3, 4, 5, 6);

println("Reduce odd: " . reduce({ return $1 + $2; }, @c));
println("Reduce even: " . reduce({ return $1 + $2; }, @d));

