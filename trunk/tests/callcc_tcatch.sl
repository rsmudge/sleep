#
# one more test of how exceptions are handled with callcc...
# 


inline causeException
{
   throw "causing an exception: $1";
}

sub bar
{
   println("Within bar!");
   causeException("haha");  
   println("After exception");
}

sub foo
{
   local('$ex');

   try
   {
      println("Pre!");
      callcc &bar;
      println("Post");
   }
   catch $ex
   {
      println("Exception $ex");
      printAll(getStackTrace());
   }
}

foo();

