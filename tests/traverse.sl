sub traverse
{
   local('$z');

   while (1)
   {
      yield "test";
      yield "uNF";

      for ($z = 0; $z < 3; $z++) { yield $z; }

      yield "blah";
   }
}

for ($x = 0; $x < 50; $x++)
{
   println(traverse());
}
