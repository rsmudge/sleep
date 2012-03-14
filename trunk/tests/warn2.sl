debug(7 | 34);

sub foo
{
   local('$handle $exception @data');

   try 
   {
      $handle = openf("jdfjsjkfjksfjksafjk");
      @data = readAll($handle);
      closef($handle);
   }
   catch $exception
   {
      warn("Couldn't open file: $exception");
      println("This is me acting really kewlios");
   }
}

foo();
