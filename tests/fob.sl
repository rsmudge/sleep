debug(7);

# phearsome Fibonacci sequence implementation.

sub fob
{
   local('%fhash');
   %fhash = ohash(0 => 0L, 1 => 1L);
   setMissPolicy(%fhash,
   {
      return long($1[$2 - 1]) + long($1[$2 - 2]);
   });
   return %fhash[$1];
}

println("Fib no. " . fob(90L));

