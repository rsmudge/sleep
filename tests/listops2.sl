@a = @("a", "b", "c", "d");
println(@a);

@a[2] = "pHEAR";
println(@a);

removeAt(@a, 2);
println(@a);

add(@a, "???", 2);
println(@a);

foreach $index => $temp (@a)
{
   println("$index => $temp");
}

@c = sublist(@a, 1);
println(@c);

shift(@c);
println(@c);
println(@a);

push(@c, "phearz");
println(@c);
println(@a);

@a = @("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p");
@b = sublist(@a, 3, 12);
@c = sublist(@b, 2, 6);
removeAt(@c, 2);
println(@a);
println(@c);

@a = @("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p");
@b = sublist(@a, 3, 12);
@c = sublist(@b, 2, 6);
removeAt(@c, 3);
println(@a);
println(@c);
println(@b);

