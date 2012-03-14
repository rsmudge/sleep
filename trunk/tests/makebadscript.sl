debug(3);

$script = 'println("hello world!");
println("this is a test");
warn("everything work ok?");';

$a = join(chr(10), split("\n", $script));
eval($a);

# sleep doesn't recognize \r by itself as a newline format...
$b = join(chr(13), split("\n", $script));
eval($b);

$c = join(chr(13) . chr(10), split("\n", $script));
eval($c);
