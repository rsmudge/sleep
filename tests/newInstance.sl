debug(7);

sub foo
{
   if ($0 eq "toString")
   {
      return "<&foo as an object>";
   }

   local('$a');

   println("I have been called with: $0");
   println("My args are:");
   foreach $n => $a (@_)
   {
      println("  $n => $a");
   }

   return 3;
}

global('$action');
$action = newInstance(^Comparable, &foo);
println("Class:      " . [$action getClass]);
println("Interfaces: " . [[$action getClass] getInterfaces]); 
println("Value:      " . $action);

debug(7 | 15);
println([$action compareTo: "foo"]);

debug(7);

$action = newInstance(@(^List, ^Comparable), &foo);
println("Class:      " . [$action getClass]);
println("Interfaces: " . [[$action getClass] getInterfaces]); 
println("Value:      " . $action);

