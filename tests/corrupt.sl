#
# reproduce a bug that allowed corrupting of constants
#

sub foo
{
   println($1);
   $1 = 3;
}

for ($x = 0; $x < 5; $x++)
{
   foo(1);
}

