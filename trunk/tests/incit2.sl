sub foo
{
   local('$__INCLUDE__');
   include("incit.sl");
}

foo();
println("Post job: $__INCLUDE__");
