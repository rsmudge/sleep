$x = 4;

$l = lambda({ println($x); }, $x => $x);

$x = 5;

[$l]; # pre Sleep2.1-b4 this would print 5 instead of 4 because lambda was sharing var containers when it shouldn't be

