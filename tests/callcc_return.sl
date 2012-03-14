#
# ensure return properly resets the coroutine... 
#

sub a
{
   callcc { return 1; };
   
   callcc { return 2; };

   if ($1 ne $null)
   {
      return -1;
   }

   callcc { return 3; };
  
   return 4; # this last return is required, if this was a yield we would resume execution of this
             # coroutine after this statement which would cause the sub to immediately return an
             # implied $null.
}

println("Test 1: ");

println(a());
println(a());
println(a());
println(a());

println(a());
println(a());
println(a());
println(a());

println("Test 2: ");

println(a());
println(a());
println(a("boogidy boogidy"));

println(a());
println(a());
println(a());
println(a());
println(a());

