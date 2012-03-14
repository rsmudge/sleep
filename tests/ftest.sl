#
# simple fork test..
#

$closure = {
   $value = 0L;
   for ($x = 0; $x < $initial; $x++)
   {
      $value = $value + long($x);
   }

   return $value;
};

$handle1 = fork($closure, $initial => 12348);
$handle2 = fork($closure, $initial => 9000);
$handle3 = fork($closure, $initial => 12345);
$handle4 = fork($closure, $initial => 47857);

println("Value 1 is: " . wait($handle1));
println("Value 2 is: " . wait($handle2));
println("Value 3 is: " . wait($handle3));
println("Value 4 is: " . wait($handle4));
