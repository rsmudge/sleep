#
# add a value to a hash.
# 

%hash = %(a => "apple", b => "boy", c => "chump");

add(%hash, d => "dog", p => 'pH34r', j => "jumping jack flash", f => { println("some function"); });
println(%hash);
