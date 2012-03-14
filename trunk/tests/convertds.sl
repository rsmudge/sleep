#
# a test of converting sleep data structures to java data structures
# (no vice versa)
#

#
# Scalar Arrays
#

# convert to a java.util.List
@test = @("a", "b", "c", "d");

println("b4 - @test is: " . @test);

$x = [Collections binarySearch: @test, "c"];

println("Found element 'c' at $x");

# convert to a java.util.Collection

$set = [new TreeSet: @("a", "b", "c", "d", "e", "f", "b1", "c1", "a1")];
println("A treeset: $set");

#
# Scalar Hashes (eeh :))
# 

# convert to a java.util.Map

$sortedmap = [new TreeMap: %(z => "zebra", a => "apple", b => "bannana", c => "cod")];
println("A sortedmap: $sortedmap");

