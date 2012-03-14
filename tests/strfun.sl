$a = "thIS is A TeST";
$b = uc($a);
$c = lc($a);

assert -isupper $b;
assert !-islower $b;

assert -islower $c;
assert !-isupper $c;

assert $a eq $a;
assert $b ne $c;
assert "a" lt "b";
assert "b" gt "a";
assert "a" !gt "b";
assert "b" !lt "a";
assert "IS" isin $a;
assert "THIS" !isin $a;

println("Done!");
