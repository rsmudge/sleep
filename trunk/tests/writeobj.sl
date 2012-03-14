#
# test object serialization/deserialization
#

$ENV = "Main Script";

$func = {
   println("Step 1 - $ENV");
   this('$rv $fact');
   $fact = { return iff($1 == 0, 1, $1 * [$this: $1 - 1]); };
   yield;

   $rv = [$fact : $number];
   println("Step 2 - $ENV");
   yield;

   println("Step 3 - $ENV");
   return $rv;    
};

$fork = fork({
   $blah = readObject($source);
   $number = readObject($source);
   [$blah];
   writeObject($source, $blah);
}, $ENV => "In fork!");

[$func];
writeObject($fork, $func, 7);
println("Value is: " . [readObject($fork)]);
