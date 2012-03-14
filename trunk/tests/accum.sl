#
# An accumulator in Sleep...
#
sub accum
{
   return lambda({
           $i = $i + $1;
           return $i; 
        }, $i => $1);
};

$a = accum(3);
$b = accum(40);

for ($x = 0; $x < 10; $x++)
{
   println("Accumulate: a: " . [$a:1] . " b: " . [$b:2]);
}
