#
# checks that closures carry their local vars with themselves.
#

sub bar
{
   local('$buffer $func');
   $buffer = allocate();
   writeObject($buffer, $1);
   closef($buffer);

   println("Normal continuation:");
   invoke($1);

   println("Serialized Continuation");
   $func = readObject($buffer);
   invoke($func);
}

sub foo
{
   println("Begin foo");
   local('$x');
   $x = "a";
   pushl();
   local('$x');
   $x = "b";
   pushl();
   local('$x');
   $x = "c";
   yield;        # pause execution
   println($x);
   popl();
   println($x);
   popl();
   println($x);
   println("End foo");
}

foo();
bar(&foo);
foo();
