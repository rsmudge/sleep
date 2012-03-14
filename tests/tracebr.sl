debug(15);

sub foo
{
   if ($1 == 1)
   {
      warn("$1 if block");
   } 


   else 










   if ($1 == 2)
   {
      warn("$1 else-if block");
   }   









   else
   {
      warn("$1 else block");
   }
}

foo(1);
foo(2);
foo(3);

