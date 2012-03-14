$buffer  = $null;

sub produce 
{
   for ($x = 0; $x < 3; $x++) 
   {
      println("Produce: $x * 3");
      $buffer = $x * 3;
      callcc &consume;
   }
}

sub consume 
{
   println("Consume: $buffer");
   [$1]; # resume the calling function
}

produce();
