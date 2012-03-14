#
# do some scalar identity fun with setop garbage.
#

$f = { println("aaa"); };
$g = { println("bbb"); };

@a = @(1, 2, 3, 4.0, 5, 6, 7, 8, "pHEAR", $f);

assert $f !=~ $g;
assert 3.0 !=~ 3;
assert 3 in @a;
assert $f in @a;
assert 45 in { local('$x'); for ($x = 0; $x < 100; $x += 5) { println($x); yield $x; } };
assert "3" !=~ 3.0;
assert "3.1" !=~ 3;
assert 3.1 =~ 3.1;
assert @a =~ @a;
assert 3.1 !=~ 3;
