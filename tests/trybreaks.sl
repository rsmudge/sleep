sub foo
{
   try
   {
      println("...");
      try
      {
         println("foo!");
         return;
      }
      catch $ex
      {
         println("foo: $ex");
      }
   }
   catch $ex3
   {
      println("foo o: $ex3");
   }
}

try
{
   foo();
   throw "test!";
}
catch $ex2
{
   println("outside: $ex2");
}
