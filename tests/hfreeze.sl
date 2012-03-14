sub foo
{
   local('$x $z');
   for ($x = 0; $x < 10000; $x++)
   {
      if ($x !in %shared)
      {
         $z++;
      }
      else
      {
         $z--;
      }
      [Thread yield];
   }
   println($z);
}

global('%shared $x @threads');

%shared[50] = 1;
%shared[60] = 1;

for ($x = 0; $x < 12; $x++)
{
   push(@threads, fork(&foo, \%shared));
}

foreach $x (@threads)
{
   wait($x);
}
