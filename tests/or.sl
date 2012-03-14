#
# Test short circuit evaluation and different ways of expressing
# OR statements to the parser.
#

$NEVER = 0;

sub nevercall
{
   $NEVER++;
   printf("I have been called $NEVER times");
   return 1;
}

$x = 2;

if ($x == 1 || nevercall())
{
   printf("I am one or never call");
}

if (($x == 1) || (nevercall()))
{
   printf("I am another one where it gets called..");
}

printf("Hi");

if ($x == 2 || nevercall())
{
   printf("Call me anytime");
}
