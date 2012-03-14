#
# check for a parser problem...
#

# this is the real bug here... &* ["test" trim] is mistaken for an indexable
# function reference or some such thing...
if ($var eq "" && ["test" trim] eq "whatever")
{
   println("hello 1");
}

if ($var eq "" || ["test" trim] eq "whatever")
{
   println("hello 1");
}

$x = 3 + ["test" length];
println($x);

$x = "blah" . ["   test" trim];
println($x);

$x = 0xFFFF & ["test" length];
