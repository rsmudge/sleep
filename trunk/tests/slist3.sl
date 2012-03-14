#
# test sublist on read only arrays.
#

$list = [new LinkedList];
[$list add: "a"];
[$list add: "b"];
[$list add: "c"];
[$list add: "d"];
[$list add: "e"];
[$list add: "f"];
[$list add: "g"];
[$list add: "h"];
[$list add: "i"];
[$list add: "j"];
[$list add: "k"];

$array = [SleepUtils getArrayWrapper: $list];

println($array);

for ($x = 0; $x < size($array); $x++)
{
   for ($y = $x; $y < size($array); $y++)
   {
      $p = 'sublist($array, ' . $x . ', ' . $y . '):';
      println("$[25]p " . sublist($array, $x, $y));
   }
}
