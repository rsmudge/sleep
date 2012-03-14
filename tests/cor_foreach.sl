#
# make sure coroutines play nicely with foreach loops.
#
sub a
{
   this('$var');

   @array = split(" ", "this is a long test string ok maybe it isn't that long oh well");
 
   foreach $var (@array)
   {
      yield "heh: $var";
   }

   return $null;
}

$a = a();
while ($a !is $null)
{
   println($a);
   $a = a();
}

