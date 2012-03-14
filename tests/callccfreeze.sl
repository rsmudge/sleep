debug(15);

sub foo
{
   callcc 
   {
      invoke($1);
   };
}

foo();
