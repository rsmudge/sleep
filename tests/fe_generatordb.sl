#
# test out foreach with closures...
#
debug(15);

foreach $index => $value ({ this('$x'); for ($x = 0; $x < 10; $x++) { yield "val: $x"; } return $null; })
{
   println("$index => $value");
}

sub _range
{
   while ($start < $stop)
   {
      yield $start;
      $start++;
   }

   return $null;
}

foreach $index => $value (lambda(&_range, $start => 50, $stop => 60))
{
   println("Testing out range stuff: $index => $value");
}

sub _range2
{
   this('$start $stop');
   ($start, $stop) = @_;
   yield $this;

   while ($start < $stop)
   {
      yield $start;
      $start++;
   }

   return $null;
}

foreach $index => $value (_range2(70, 80))
{
   println("My third test: $index => $value");
}
