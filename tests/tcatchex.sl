sub foo
{
   if ($1 < 0)
   {
      exit("Time to leave: $1");
   }

   return foo($1 - 1);
}

try
{
   foo(5);
}
catch $end
{
   println("err... $end");
}

println("We're done now, whee...");
