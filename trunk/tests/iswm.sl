#
# tests for the iswm algorithm...
#

println("Two empty strings: " . iff("" iswm "", "true", "false"));
println("One empty strings: " . iff("aaaa" iswm "", "true", "false"));

println("Case 1 : " . iff("*aa*" iswm "bbaabb", "true", "false")); # true
println("Case 2 : " . iff("?aa?" iswm "bbaabb", "true", "false")); # false


println("Case 3 : " . iff("aa" iswm "bbaabb", "true", "false"));   # false

println("Case 4 : " . iff("bb" iswm "bbaabb", "true", "false"));   # false

println("Case 5 : " . iff("*bb" iswm "bbaabb", "true", "false"));  # non-greedy - false
println("Case 5g: " . iff("**bb" iswm "bbaabb", "true", "false"));  # greedy     - true

println("Case 6 : " . iff("*cc" iswm "bbaacc", "true", "false"));  # true 

println("Case 7 : " . iff("aa*b" iswm "aabbbb", "true", "false"));  # false - non-greedy
println("Case 7g: " . iff("aa**b" iswm "aabbbb", "true", "false")); # true  - greedy

println("Case 8 : " . iff("aabb*bb" iswm   "aabbbb", "true", "false"));   # true - since the * matches 0 or more chars
println("Case 8a: " . iff("aabb?**bb" iswm "aabbbb", "true", "false")); # false
println("Case 8b: " . iff("aabb?*bb" iswm  "aabbbb", "true", "false"));  # false
println("Case 8c: " . iff("aabb?*bb" iswm  "aabbbbb", "true", "false"));  # true
println("Case 8d: " . iff("aabb?**bb" iswm "aabbbbb", "true", "false"));  # true
println("Case 8e: " . iff("aabb?bb" iswm   "aabbbbb", "true", "false"));  # true

println("Case 9 : " . iff('aa\*bb' iswm "aa*bb", "true", "false")); # true
println("Case a : " . iff('aa\?bb' iswm "aa*bb", "true", "false")); # false

println("Case b : " . iff('aabb\\' iswm "aabb\\", "true", "false")); # true

println("Case c : " . iff("J?ck" iswm "Jack", "true", "false")); # true
println("Case d : " . iff('J\?ck' iswm "Jack", "true", "false")); # false
println("Case e : " . iff('J\?ck' iswm "J?ck", "true", "false")); # true


