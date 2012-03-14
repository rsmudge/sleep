#
# ensure if statement condition gets evaluated only once in a coroutine...
#

sub check
{
   println("Check function has been called!@#$%^&");
   return 1;
}

sub a
{
   if (check())
   {
      callcc { return "within if statement"; };
   }

   return "done";
}

println("Call 1: " . a());
println("Call 2: " . a());

sub b
{
   if (check())
   {
      callcc { return "within if statement"; };
      println("The if should continue anyways...");
   }

   return "done";
}

println("Call 3: " . b());
println("Call 4: " . b());


