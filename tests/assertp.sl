#
# Sleep script that tests recursion.
# Computes the factorial of a number.
#

debug(15);

sub fact
{
   assert $1 >= 0 : "argh! tied to pass a 0 value"; 
   return $1 * fact($1 - 1);
}

printf("fact(6) is: " . fact(6));
