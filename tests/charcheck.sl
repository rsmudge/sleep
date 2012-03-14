#
# ensure that characters are matched properly and double check the semantics of boolean
#

println(iff([Character isLetter: "a"], "true", "false"));
println(iff([Character isLetter: "abc"], "true", "false"));
println(iff([Character isLetter: "7"], "true", "false"));
println(iff([Character isLetter: 7], "true", "false"));
println(iff([Character isDigit: 7], "true", "false"));
println(iff([Character isDigit: "7"], "true", "false"));

println("- " x 20);

println([Boolean toString: 1]);
println([Boolean toString: 0]);

println("-" x 10);

println([Boolean toString: "true"]);
println([Boolean toString: "false"]);

println("-" x 10);

println([Boolean toString: $null]);

println("-" x 10);

println([Boolean toString: "dsfdsfsf"]);

println("-" x 10);

println([Boolean toString: 1.0]);
println([Boolean toString: 0.0]);

println("-" x 10);

println([Boolean toString: "test"]);
println([Boolean toString: "not truth"]);

println("-" x 10);

println([Boolean TRUE]);
println([Boolean FALSE]);

