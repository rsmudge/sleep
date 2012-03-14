#
# callcc... pHEAR
# 

debug(15);

sub foo
{
   println("foo start: $this");
   callcc { 
       println("Hello Continuation: $1"); 
       [$1: "from anonymous function!"];
       return "this is foo's new value";
   };
   println("foo postcc - $1");
}

global('$value');
$value = foo();
println("Does this ever happen? $value");

debug(7);

global('$buffer $object');

$buffer = allocate();

inline serialize
{
   callcc {
      writeObject($buffer, $1);
      println("Continuation $1 saved to buffer...");
   };
}

sub bar
{
   local('$a');
   $a = 3456;
   println("this is bar: $a");
   serialize();
   println("I am alive from the dead... $a");
   return $a;
}

bar();
println("Bar is done :)");
closef($buffer);

$object = readObject($buffer);
println([$object]);

#
