#
# testing the closure concept...
#

sub my_func
{
   return "this is a function: $1 , $2 and $3";
}

$my_closure = { return "this is from the closure: $1 , $2 and $3"; };

println("Subroutine: " . [&my_func test: "S argument 1", "S argument 2", "S argument 3"]);
println("Closure:    " . [$my_closure test: "C argument 1", "C argument 2", "C argument 3"]);

