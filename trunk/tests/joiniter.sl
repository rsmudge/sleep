#

println(join(", ", { local('$x'); for ($x = 0; $x < 10; $x++) { yield $x; } }));

sub foo
{
   local('$x @a'); 
   for ($x = 0; $x < 10; $x++)
   {
      @a = @(1, 2, 3);
      yield @a;
   }
}

println(flatten(&foo));

println(search(flatten(&foo), { if ($1 == 3) { return 3; } }));
