#
# bug with profiler only mode...
#

debug(7 | 24);

global('$x');

$x = [new java.util.LinkedList];
[$x remove: 3];
