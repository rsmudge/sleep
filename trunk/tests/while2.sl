#
# test the extended syntax for while loops...
#

sub callto
{
   this('$x');
   $x = 0;
   while ($x < $1)
   {
      yield $x;
      $x++;
   }

   return $null;
}

sub repeat
{
   println("The repeat function has been called");
   return 10;
}

while $check (callto(repeat()))
{
   println("Testing new while syntax: $check");
}

# test 2.

$handle = openf("while2.sl");

while $value (readln($handle))
{
   println("Read: $value");
}
