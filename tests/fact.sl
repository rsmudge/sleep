#
# Sleep script that tests recursion.
# Computes the factorial of a number.
#

sub fact
{
   if ($1 eq 0)
   {
      return 1;
   }
   else 
   {
      return $1 * fact($1 - 1);
   }
}

printf("fact(6) is: " . fact(6));
