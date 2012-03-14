#
# more tests of the ever loveable buffer..
#

debug(7);

global('$handle');

$handle = allocate(1024 * 1024); # 1K buffer...

println($handle, "this is a test");
println("Number of bytes available to read? " . available($handle));

println($handle, "this is another test :)");

println("Trying a read: " . readln($handle)); # should fail!

closef($handle); # the buffer is now readable...

println("Bytes avail: " . available($handle));

mark($handle, 1024);

println(strrep(readb($handle, available($handle)), "\n", '\n'));
println("Bytes avail (post read): " . available($handle));

println("--- Reading the two sentences ---");
reset($handle);
mark($handle);

println("Reading a single byte: " . readb($handle, 1));

println("Bytes avail: " . available($handle));
println(readln($handle)); 

println("Bytes avail: " . available($handle)); # this is 0 for some reason and I'm not sure why...
                                               # seems readln and friends disturb the force?
 
                                               # now I know why!! it appears that ops to read unicode
                                               # chars will suck up some of the buffer contents to
                                               # make things more "efficient". 

println(readln($handle)); 
println("Bytes avail: " . available($handle));

println("--- Final Read Step ---");
reset($handle);
println("Bytes avail: " . available($handle));
printAll(readAll($handle));
