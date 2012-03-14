#
# Trouble calling methods on inner classes, should be fixed...
# 

$list = [new LinkedList];
[$list add: "item 1"];
[$list add: "item 2"];
[$list add: "item 3"];

$iter = [$list iterator];

while ([$iter hasNext])
{
   println("Testing: " . [$iter next]);
}
