#
# test of string functions...
#


println(right("this is a test", 4));  # the right most 4 chars
println(left("this is a test", 4));   # the left most 4 chars

# expected:
# test
# this

println(right("this is a test", -5)); # all right chars except the left 5
println(left("this is a test", -5));  # all left chars except the right 5

# expected:
# is a test
# this is a

println(replaceAt("this is a test", "uNF", -6, 1));

# expected:
# this is uNF test

println(charAt("this is a test", -6));

# expected:
# a

$len = strlen("this is a test");

for ($x = $len * -1; $x < $len; $x++)
{
   print(charAt("this is a test", $x));
}

println();

# reverse a string quickly with the negative indice bits...
$str = "++this is a reversible string :)--";
for ($x = strlen($str) - 1; $x >= 0; $x--)
{
   print(charAt($str, $x));
}

println();

println(substr($str, -11, -5));

println(mid($str, -11, 6));

[{ println(substr($str, -11, -15)); }];

for ($x = 0; $x < $len; $x++)
{
   println(indexOf("this is a test", "i", $x));
}
