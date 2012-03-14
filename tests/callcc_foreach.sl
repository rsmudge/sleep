#
# make sure coroutines play nicely with foreach loops.
#
sub b
{
   println($value);
   return invoke($1);
}

sub a
{
   local('@array');
   this('$var');

   @array = split(" ", "this is a long test string ok maybe it isn't that long oh well");
 
   foreach $var (@array)
   {
      callcc lambda(&b, $value => "heh: $var");
   }

   return "done!!!";
}

println("checking: " . a());
