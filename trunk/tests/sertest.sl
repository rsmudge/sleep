#
# test serialization of the arrays.
#

@a = @("a", "b", "c", "d", "e");
@b = sublist(@a, 2, 4);

println("Prior to serialization:");
println(@a);
println(@b);

$handle = allocate();
writeObject($handle, @a);
writeObject($handle, @b);
closef($handle);

#println("Buffer size: " . available($handle));

@aa = readObject($handle);
@bb = readObject($handle);

println("Post serialization");
println(@aa);
println(@bb);

push(@bb, "hello world!");

println("The push!");

println(@aa);
println(@bb);

println("And for the originals");
println(@a);
println(@b);
