#
# force java types through scalar conversion process..
#

# no dice, can't do math on a java.lang.Double
$double = [new Double: 3.4567];
$a = 3 + $double;
println($a);

# this will work though.
$a = 3 + scalar($double);
println($a);

# byte arrays got me down?
$data = cast("this is a test", "b");
println("$data and " . [$data getClass]);

$d2 = scalar($data);
println("$d2 and " . [$d2 getClass]);
