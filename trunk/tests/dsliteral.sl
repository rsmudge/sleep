#
# literal instantiation of sleep data structures
#

@array = @("a", "b", "c", "d", "e", "f");
printAll(@array);

#

printAll(@( "this",
  "is", 
  "a",
  "test",
  "of",
  "a",
  "new",
  "syntax"));

#

foreach $key => $value (%(a => "apple", b => "bannana", c => "cambodia", e => "elephant"))
{
   println("$key => $value");
}

#

println("This is a test: " . @("a", "b", "c", @("d", "e", "f"), "g")[3][2]);
