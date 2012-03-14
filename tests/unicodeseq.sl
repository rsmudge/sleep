#
# test specifying unicode literal strings...
#

debug(3);

$x = "this is \u0041 test :)";
println($x);

$x = "this is \u0041 test :)\u0041\u0041\u0041\u0041\u0041X\u0041";
println($x);

eval('
   $x = "this is a \u004 test";
');

eval('
   $x = "this is a \u004 test \u004";
');

eval('
   $x = "this is a \uAZBCDEM test \u004";
');

#
#
#

$x = "this is \x41 test :)";
println($x);

$x = "this is \x41 test :)\x41\u0041\x41\x41\x41X\x41";
println($x);

eval('
   $x = "this is a \x4 test";
');

eval('
   $x = "this is a \x4 test \x4";
');

eval('
   $x = "this is a \xAZBCDEM test \x4";
');

