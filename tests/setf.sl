#
# test setting named closures...
#

setf('&foo', { println("foo"); });
foo();

setf('&foo', $null);
foo();

sub bar
{ 
   println("bar");
}

setf('&foo', &bar);
foo();

[{ setf('&foo', "ninja moves"); }];
[{ setf('foo', { println("eh?!?"); }); }];

foo();

[function('&foo')];

$x = &foo;

setf('&foo', { println("not foo"); });

[$x];
foo();

setf('&foo', $x);
foo();
