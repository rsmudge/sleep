# create a memoize'd version of the specified function:
sub memoize
{
   local('%cache');   

   %cache = ohash();

   setMissPolicy(%cache,
      lambda({
                println("Cacheing: $2");
                return invoke($function, $2);
             }, $function => function($1)));

   setf($1, lambda({ return %cache[@_]; }, \%cache));
}

sub fib
{
   if ($1 == 0)
   {
      return 0L;
   }
   else if ($1 == 1)
   {
      return 1L;
   }
   else
   {
      return fib($1 - 1) + fib($1 - 2);
   }
}

# implement memomizing rewTINEZ
memoize('&fib');

println("(Memoized) Fib no. " . fib(25L));
