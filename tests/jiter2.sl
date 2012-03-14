#
#

$list = [new LinkedList];
[$list add: "a"];
[$list add: "b"];
[$list add: "c"];
[$list add: "d"];
[$list add: "e"];

foreach $count => $value ([$list iterator])
{
   println("$count and $value");

   if ($count > 2)
   {
      remove();
   }
}

println($list);
