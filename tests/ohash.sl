#
# test of the pHEARsome ordered hash.
#

%hash = ohash(a => "apple", c => "cat", d => "dog", p => "pHEAR", aa => 33, b => 56L);
println(%hash);

foreach $key => $value (%hash)
{
   println("$[10]key is $value");
}

%hash["a"] = $null;
%hash["a"] = "ordered insert...";  # in this case a will just be updated.

println(%hash);

%hash["a"] = $null; 

println(%hash);

%hash["a"] = "at the end";

println(%hash);

%phear = %hash;
%phear["zzz"] = "this is at the end of phear";
println(%phear);

%copy = ohash(begin => "front end");
putAll(%copy, keys(%phear), values(%phear));

println(%copy);
println(%phear);
