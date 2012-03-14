#
# Test of fork isolation...
#

#
# check the isolation of the global scope...
#
$global = "this is a global variable in the main script";

sub fork_me
{
   $global = "this is a global variable in the closure script";
   println("From fork_me: $global");
}
  
println("Main script: $global");

fork(&fork_me);

sleep(1000); # for sure let the first closure run...

println("Main script: $global");
	
#
# check the isolation of the this scope
#
sub closure
{
   this('$x');
   $x++;

   return $x;
}

closure();
closure();
closure();

println("Main script: " . closure());

fork({
   println("Forked: " . closure());
});

sleep(1000);

println("Main script: " . closure());

