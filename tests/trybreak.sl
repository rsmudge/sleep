#
# a unit test to check that break still causes exception context to get popped.
#

debug(7);

sub foo
{
   local('$exception');

   try
   {
      bar();
   }
   catch $exception
   {
      warn("CORRECT: $exception");
      printAll(getStackTrace());
   }
}

sub bar
{
   local('$x $ex');

   for ($x = 0; $x < 10; $x++)
   {
      try
      {
         println($x);
         if ($x == 5)
         {
            break;
         }
      }
      catch $ex
      {
         warn("WRONG ONE: $ex");
      }
   }

   throw "done with loop!";
}

foo();
