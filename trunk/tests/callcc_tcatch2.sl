#
# one more test of how exceptions are handled with callcc...
# 

# this control flow will really mess with your head... trust me.

inline causeException
{
   throw "causing an exception: $1";
}

sub bar
{
   println("Within bar!");
   invoke($1);
   println("After exception");
   return 42;
}

sub foo
{
   local('$ex');

   try
   {
      println("Pre!");
      callcc &bar;
      causeException("haha");  
      println("Post");
   }
   catch $ex
   {
      println("Exception $ex");
      printAll(getStackTrace());
   }
}

println(foo());

