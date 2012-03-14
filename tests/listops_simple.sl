@list = @("a", "b", "c", "d", "e");
println(@list);

@sublist = sublist(@list, 1, 4);
println(@sublist);

removeAt(@sublist, 0);
println(@sublist);

removeAt(@sublist, 0);
println(@sublist);

removeAt(@sublist, 0);
println(@sublist);

add(@sublist, "blah");
println(@sublist);

println(@list);
