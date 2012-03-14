#
# a test of compile_closure with its new nifty improvements.
#

sub bar { }
&bar['$x'] = "bars x var is kewlios";

$foo = compile_closure('
   println("Hello from $name $+ !");
   println($x);
', $this => &bar, $name => "'new closure'");

println($foo);
[$foo];
