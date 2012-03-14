debug(7);

global('$x');

sub foo
{
   local("$x $y $z");
}

$x = 33;
foo();
