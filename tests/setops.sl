#
# doing a test of some set operations...
#

sub b
{
   return array(1, 2, 3, 4, 5, 6);
}

sub evens_odds
{
   return array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
}

sub evens
{
   return array(2, 4, 6, 8, 10, 12);
}

sub odds
{
   return array(1, 3, 5, 7, 9, 11);
}

println("--- Add All || Set Union ---");
println("Test 1"); # ensure that only one of each element is in @a
@a = evens();
@b = b();
addAll(@a, @b);
printAll(sortn(@a));

println("Test 2"); # ensure that all elements are in @a
@a = evens();
@b = odds();
addAll(@a, @b);
printAll(sortn(@a));

println("Test 3"); # yet another test...
@a = evens();
@b = evens();
addAll(@a, @b);
printAll(sortn(@a));

println("--- Remove All || Set Difference ---");
println("Test 1"); # ensure that only one of each element is in @a
@a = evens_odds();
@b = evens();
removeAll(@a, @b);
printAll(sortn(@a));

println("Test 2"); # ensure that all elements are in @a
@a = evens();
@b = odds();
removeAll(@a, @b);
printAll(sortn(@a));

println("Test 3"); # yet another test...
@a = evens();
@b = evens();
removeAll(@a, @b);
printAll(sortn(@a));

println("--- Retain All || Set Intersect ---");
println("Test 1"); # ensure that only one of each element is in @a
@a = evens_odds();
@b = evens();
retainAll(@a, @b);
printAll(sortn(@a));

println("Test 2"); # ensure that all elements are in @a
@a = evens();
@b = odds();
retainAll(@a, @b);
printAll(sortn(@a));

println("Test 3"); # yet another test...
@a = evens();
@b = evens();
retainAll(@a, @b);
printAll(sortn(@a));

	

