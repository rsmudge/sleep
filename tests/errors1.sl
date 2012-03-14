#
# Test the error catching bits.
#

sub foo
{
   if (1)
   {
      printf("I love food...);  # run away string... will the parser catch it?
   }
}

foo();

printf("uNF... ?");
