#
# a test of the scalar references... pHEAR
#

sub atest
{
   local('@array %hash');
   @array = @("a", "b", "c");
   %hash  = %(x => "xxx", y => 123, z => 45L);
   foo(\@array, \%hash); # pass these values directly into &foo
}

sub foo
{
   local('@array %hash'); # passed as named params hopefully.

   println(@array);
   println(%hash);
}

$x = 3;
$y = "a string";
$z = 0x47893498L;

println(%(\$x, \$y, \$z));
atest();

$c = let({ invoke(&blah, \$this); }, $xx => "xx in closure c", $yy => "yy in closure c");

sub blah
{
   println("$xx and $yy");   
   println($this);
}

blah();
[$c];

println(\$c);
println(\$x . \$y . \$z);

eval('println(\$null);');
println(checkError());
