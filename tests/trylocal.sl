#
#
#

sub bar
{
   throw "hehehe....";
}

sub foo
{
   local('$ex');

   try
   {
      bar();
   }
   catch $ex
   {
      println("$ex");
      printAll(getStackTrace());
   }
}

foo();
