sub foo
{
   try
   {
      println("...");
      try
      {
         println("foo!");
         yield "blah";
         throw "!!!! 1";
      }
      catch $ex
      {
         println("foo: $ex");
      }
 
      throw "!!!! 2";
   }
   catch $ex3
   {
      println("foo o: $ex3");
      throw "!!! 3";
   }
}

try
{
   foo();
   try
   {
      foo();
   }
   catch $ex4
   {
      println("caught: $ex4");
      throw "test!";
   }
}
catch $ex2
{
   println("outside: $ex2");
}
