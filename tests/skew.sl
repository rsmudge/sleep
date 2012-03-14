#
# a test of more line number skew...
#

debug(7);

sub funky
{
   println("
   some stuff here: " . $1 . "
   some more stuff
   and more stuff
   and even more stuff...
   " . $2 . "

   ");
}

funky();

$x = '

  this is a  test';
