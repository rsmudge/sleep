debug(7);

sub foo
{
   include("incit.sl");
}

foo();
println("Post job: $__INCLUDE__");
