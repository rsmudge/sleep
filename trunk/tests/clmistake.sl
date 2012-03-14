#
# most common problem with closures... people forgetting to put a damned
# semicolon after the literal form!!!
#

debug(7);
global('$func');

eval('
   #
   #
   $func = { println("foo!"); }
   [$func];
');

sub bar
{
   println("bar!");
}

eval('
   #
   #
   #
   $func = { println("foo!"); }
   bar();
');
