#
# Test of the format for the new foreach loop...
#

%hash  = hash(x => "x-men", s => "spiderman", g => "guardians of the galaxy", h => "the incredible hulk");
@array = array("The Incredible...", "The Amazing...", "The Lame...");

foreach $key => $value (%hash)
{
   println("$[10]key = $value");
}

foreach $index => $data (@array)
{
   println("$[10]index = $data");
}

foreach $test (%hash)
{
   println("Iterate over hash: $test");
}
