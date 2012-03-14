#
# this file replicates a bug found in Sleep 2.1 where many array functions were not
# creating a new scalar container with a reference to a scalar value rather they shared
# the old scalar container they were passed resulting in all kinds of unpredictable behavior
# 
# push took the right measures to copy but functions like add and many others did not.
#
debug(7);
global('@a @b $temp1 $temp2');

#
# test 1
#
$temp1 = "this is a test";
push(@a, $temp1);

println("Before: Contents of @a (temp1 is $temp1 $+ )");
printAll(@a);

@a[0] = "this is a changed value";

println("After: Contents of @a (temp1 is $temp1 $+ )");
printAll(@a);


#
# test 2
#
$temp2 = "this is a test";
add(@b, $temp2);

println("Before: Contents of @b (temp2 is $temp2 $+ )");
printAll(@b);

@b[0] = "this is a changed value";

println("After: Contents of @b (temp2 is $temp2 $+ )");
printAll(@b);



