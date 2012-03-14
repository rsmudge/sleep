#
# more miscellaneous closures... (taken mostly from the sleeplang document)
#

# 1

$closure = {
   println("My name is: $1"); 
};

[$closure: "Raphael"];

# 2

sub my_sub {
   println("My name is: $1");
}

[&my_sub: "Raphael"];

# 3

$myfunc = lambda({
   println("My initial name is $name");
},  $name => "Raffi");

[$myfunc];

# 4

sub accum 
{
   return lambda(
          {
             $i = $i + $1; 
             return $i;
          },  $i => $1);
}

$accum_a = accum(3);
$accum_b = accum(30);

println("a: " . [$accum_a: 1]);
println("b: " . [$accum_b: 2]);

println("a: " . [$accum_a: 1]);
println("b: " . [$accum_b: 2]);

# 5

sub BuildStack
{
   return {
             this('@stack');

             if ($0 eq "push") { push(@stack, $1); }
             if ($0 eq "pop")  { return pop(@stack); }
          };
}

$mystack = BuildStack();  # construct a new stack closure...
[$mystack push: "test"];  # push the string "test" onto the stack

println("Top value is: " . [$mystack pop]); # pop the top value off of the stack and print it

# 6 - not a closure example per se, but worth looking at

import java.util.*;

$scalar = [new StringTokenizer: "this is a test", " "];

while ([$scalar hasMoreTokens])
{
   println("Token: " . [$scalar nextToken]);
}
