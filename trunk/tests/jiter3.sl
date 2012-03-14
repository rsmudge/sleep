$list = [new LinkedList];
[$list add: 1];
[$list add: 3];
[$list add: 9];
[$list add: 12];

println(map({ return $1 * 3; }, [$list iterator]));

