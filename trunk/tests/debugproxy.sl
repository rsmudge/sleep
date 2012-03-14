#
# debug with proxy instances.
# 

local('$object $object1');

debug(7 | 15);

$object = newInstance(@(^List, ^Set), { warn("Method called: $0 " . @_); });
[$object add: "x"];

$object1 = newInstance(^List, { warn("Method called: $0 " . @_); });
[$object1 add: "x"];

