#
# Test of clearing hashes and arrays and removing elements from hashes/arrays
#

println("Pre Removal:");

#%hash = hash("key=hello world!@", "blah=bleh", "user=password", "skanks", "testing", "", "zing=1+2=3 or 4");
%hash = hash("key=hello world!@", "blah=bleh", "user=password", "zing=1+2=3 or 4");

removeAt(%hash, "user");

foreach $var (keys(%hash))
{
   println("   $[10]var = ".%hash[$var]);
}

println("Post Removal:");
clear(%hash);

foreach $var (keys(%hash))
{
   println("   $[10]var = ".%hash[$var]);
}

