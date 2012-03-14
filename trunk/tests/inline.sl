#
# test out inline functions... pHEAR continuation like behavior.
#

# simplest test

inline foo
{
   println("foo");
   return "foo rules!";
}

sub bar
{
   println("bar");
   foo();
   return "bar rules";
}

println(bar());

# coroutines test

inline blah
{
   println("blah 1");
   yield "pHEAR";
   println("blah 3");
}

sub bleh
{
   println("bleh 0");
   blah();
   println("bleh 2");
}

while $value (bleh())
{
   println("Returned $value");
}

# inline locals access and arguments test.

inline ltest
{
   println("@_ = " . @_);
   println("Arg: $1");
   println("\$foo: $foo");
   println("\$this: $this");
   println("\$key: $key");
   $foo = "pHEAR";
}

sub lfunc
{
   local('$foo');
   $foo = 'uNF';
   ltest("aa", "bbb", $key => "some value", "cccc");
   println($foo);
}

lfunc();

#
# traces..
#

debug(debug() | 15);
bar();
debug(debug() & not(15));

#
# exceptions..
#

inline except
{
   throw 33;
}

sub extest
{
   local('$ex');

   try
   {
      except();
   }
   catch $ex
   {
      println($ex);
      printAll(getStackTrace());
   }
}

extest();

#
# test out serialization of a coroutine using inline function calls
#

sub testit
{
   local('$object $buffer');

   $buffer = allocate();
   $object = lambda(&bleh);
   println("Result: " . [$object]);   

   writeObject($buffer, $object);
   closef($buffer); # close write-mode, enable read-mode

   $object = readObject($buffer);
   println("Result: " . [$object]);

   closef($buffer); # close buffer for all time
}

testit();
