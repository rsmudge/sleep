#
# test out taint mode with serialization
#

debug(7);

local('$buffer %hash $temp');

%hash   = %(a => "apple", b => "bats", c => "cats");
$buffer = allocate();
writeObject($buffer, %hash);
closef($buffer);

$temp = readObject($buffer);
println($temp);
println(keys($temp));
println(values($temp));

