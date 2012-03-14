# Test operator precedence
#
# - I'm known for jacking up operator precedence when writing a parser or a
# grammar.  So its important that this test is in place.
#
# In sleep only +, -, and . have built in higher precedence than other operators.
# An operator is very loosely defined in sleep as anything in between two expressions,
# values, or variables.   
#

$a = 9;
$b = 7;
$c = 42;

# + *

$value = $a + $b * $c;  # 303
printf($value);

$value = $a * $b + $c;  # 105  (it would be 441 w/o my precedence hack - 441 is incorrect)
printf($value);

$value = $a + $b + $c;  # 58
printf($value);

$value = $a * $b * $c;  # 2646
printf($value);
