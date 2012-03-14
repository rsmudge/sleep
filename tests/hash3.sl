#
# Test of clearing hashes and arrays and removing elements from hashes/arrays
#

%hash = hash(
          key    => "hello world!@",
          blah   => "bleh",
          skanks => 3.0 * (245 % 45),
          "old style=this is a literal string"
        );

foreach $var (keys(%hash))
{
   println("   $[10]var = ".%hash[$var]);
}
