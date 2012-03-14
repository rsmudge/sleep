#
# Make sure local scope works...  whirred.
#

sub test
{
   local('$x $y');
   $x = 5;
   $y++;
   printf("Inside of the function test: \$x is $x and \$y is $y");
}

sub test2
{
   printf("I don't have any locals, just global \$x - $x");
}

$x = 3;
printf("1. Outside of functions: \$x is $x");
test();
test();
test();
test2();
printf("2. Outside of functions: \$x is $x");


