#
# some cases with try/catch, its fun, I promise...
#
sub foo
{
   local('$c');
   $c = {
      throw "this is being thrown because I can!!!";
   };
   println("foo(): \$c is $c");
   [$c];
   println("Never reached (in theory)");
}

#
# test normal case, please...
#
sub bar
{
   local('$lvar');
   $lvar = "locals are visible (as they should be)";

   try
   {
      println("Outside of try block.");

      try
      {
         foo(); 
      }
      catch $ex1
      {
         println("1. Caught: $ex1 - $lvar");
      }

      println("Post try block");
   }
   catch $exception
   {
      println("0. Caught: $exception");
   }
}

bar();

#
# test the rethrowing of an exception...
#
sub fee
{
   local('$lvar');
   $lvar = "locals are visible (as they should be)";

   try
   {
      println("Outside of try block.");

      try
      {
         foo(); 
      }
      catch $ex1
      {
         println("1. Caught: $ex1 - $lvar");
         throw $ex1;
      }

      println("Post try block");
   }
   catch $exception
   {
      println("0. Caught: $exception");
   }
}

fee();

#
# test coroutines and exceptions
#
sub bee
{
   try
   {
      println("Outside of try block.");

      yield "a. checkpoint 1";

      try
      {
         yield "b. pre foo";
         foo(); 
         yield "c. post foo";
      }
      catch $ex1
      {
         println("....");
         yield "d. in exception block: $ex1";
         println("1. Caught: $ex1 - $lvar");
         yield "e. pre throw :)";
         throw $ex1;
      }

      yield "f. post catch block";
      println("Post try block");
   }
   catch $exception
   {
      yield "g. outermost catch block";
      println("0. Caught: $exception");
   }

   println("this outermost block isn't hosed, is it?");
}

while $test (bee())
{
   println("--- $test");
}
