#
#  this is a unit test locking in place what the current behavior is for evaluation
#  order of operands/arguments...
#
#  for better or worse here is what we are stuck with:
#  1. predicate operators evaluate the left hand side first, then the right hand side
#  2. functions evaluate their last argument first, and their first argument last
#     (so in reverse order)
#  3. operators evaluate their right hand side first, then their left hand side
#
#  to "fix" this behavior would require work within the interpreter and every bridge used
#  to grabbing their arguments off of a stack.  I've investigated changing this but
#  to change it now could break too many things.  For now I've opted to not make the
#  change.
#

sub buildArray
{
   return @(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
}

println(buildArray());

@test = buildArray();
println(shift(@test) . shift(@test) . shift(@test) . shift(@test));

sub fun1
{
   println("Function 1 called!");
   return $null;
}

sub fun2
{
   println("Function 2 called!");
   return $null;
}

if (fun1() eq fun2())
{
   println("IF test 1");
}

if (fun1() || fun2())
{
   println("IF test 2");
}

@test = buildArray();
println("Test: " . @(shift(@test), shift(@test), shift(@test), shift(@test)));

sub foo { }

foo(fun1(), fun2());
