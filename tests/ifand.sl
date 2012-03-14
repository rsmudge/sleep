#
# Another test of "if" statements... lets make sure I didn't break anything, eh?
#

if (3 == 1 && 3 == 2)
{
   printf("Never true 1");
}

if (3 == 3 && 4 == 4)
{
   printf("Always True 2");
}

if ((3 == 1) && (3 == 2))
{
   printf("Never true 3");
}

if (3 == 3 && (4 == 4))
{
   printf("Always True 4");
}

if ((3 == 3) && 4 == 4)
{
   printf("Always True 5");
}

if (-isnumber 5 && 6 == 6)
{
   printf("Test?");
}

if (-isnumber 5 && -isnumber 7)
{
   printf("I should be true as well");
}

if (-inumber 5 && -isnumber 6 && -isnumber 7 && -isnumber 8 && -isnumber 9 && -isnumber 385839)
{
   printf("Testing, this is true");
}

if (3 == 4 || (5 == 5 && -isnumber 10))
{
   printf("Definetly true");
}
