#
# showing error messages for attempts to index non-indexable stuff...
#

%hash["x"]["y"]["z"] = "hello world!";
println(%hash);

%hash["x"]["y"] = %hash["x"]["y"] . "oops!";
println(%hash);

println("Value is: " . %hash["x"]["y"]["z"]);
