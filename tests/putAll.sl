#
# a test of the putAll function...
#

# initialize a hash?
@a = @("a", "apple", "b", "boy", "c", "cat", "d", "dog", "x", "xenophobe", "aa", "appreciative ardvark");
putAll(%hash, @a);

println("%hash is now: " . %hash);

# some easy way to copy a hash?
putAll(%hash2, keys(%hash), values(%hash));
println("%hash2 is now: " . %hash2);

# some easy way to clear a hash... 
putAll(%hash2, keys(%hash2), { return $null; });
println("%hash2 is now: " . %hash2);

# some easy way to set all values to something else...
putAll(%hash2, keys(%hash), { return 1; });
println("%hash2 is now: " . %hash2);

# one more test...
putAll(%hash2, {
   this('$x');
   for ($x = 0; $x < 10; $x++)
   {
      yield $x;
   }
});

println("%hash2 is now: " . %hash2);

clear(%hash2);
println("%hash2 is now: " . %hash2);
