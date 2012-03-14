#
#
#

%data = %(a => "apple", b => "bat", c => "cat");
@temp = values(%data);

println(@temp);
println(%data);

@temp[1] = "blah";
println(@temp);
println(%data);

%cache = ohasha();
setMissPolicy(%cache, { return uc($2); });

println(values(%cache, @("apple", "ardvarks", "dogs", "duDeS")));
