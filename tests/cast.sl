#
# some tests of cast with its new found ability to love class literals
#

sub printIt
{
   println("$1 " . [$1 getClass]);
}

global('@a');
@a = cast(@("this is a string", "this is a test", "blah"), ^CharSequence);
printIt(@a);

import java.util.*;

@a = cast(@([new LinkedList], [new ArrayList]), ^List);
printIt(@a);
