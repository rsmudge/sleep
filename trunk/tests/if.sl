# test some stuff with if statements...

sub sluts
{
   if ($1 eq "a" || $1 eq "d")
   {
      return "skanks";
   }

   if ($1 eq "b")
   {
      return "sluts";
   }

   if ($1 eq "c")
   {
      return "whores";
   }
 
   return "bitches";
}

printf("a: " . sluts("a"));
printf("b: " . sluts("b")); # ho ho I'm another comment
printf("c: " . sluts("c"));
printf("d: " . sluts("d"));
printf("e: " . sluts("e"));

