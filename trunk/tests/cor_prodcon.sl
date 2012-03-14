#
# Producer/Consumer Problem with Coroutines...
#

@data = array();

sub produce
{
   this('$x');

   println("*** Starting to Produce ***");

   for ($x = 0; $x < 100; $x++)
   {
      println("Produce: $x");
      push(@data, $x);

      if (($x % 4) == 0 && $x > 1)
      {
         yield;
      }
   }

   yield; # give up the last stuff we produced...

   clear(@data); # produce no data when we're ready to cancel...
}

sub consume
{
   this('$var');

   println("*** Starting to Consume ***");

   while (1)
   {
      while (size(@data) > 0)
      {
         $var = pop(@data);
         println("Consume: $var");
      }

      yield; 
   }   
}

produce();
while (size(@data) > 0)
{
   consume();
   produce();
} 

