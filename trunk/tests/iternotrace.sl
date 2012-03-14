debug(15);

sub generator
{
   local('$x');

   for ($x = 0; $x < 5; $x++)
   {
      yield $x * 2;
   }
}

sub foo
{
   foreach $data (&generator)
   {
      println("Received $data");
   }

   map({ println("More fp: $1"); }, &generator);
}

foo();
