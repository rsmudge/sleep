#
# test out readAsObject and writeAsObject
#

$buffer1 = allocate(1024);
writeObject($buffer1, "this is a string");
closef($buffer1);

$test1 = readAsObject($buffer1);
println([$test1 getClass]);

###############

$buffer2 = allocate(1024);
writeObject($buffer2, "this is a string");
closef($buffer2);

$test2 = readObject($buffer2);
println([$test2 getClass]);

