#
# tests for the iswm algorithm...
# making sure that when lhs (wildcard pattern) is longer than rhs the results still workout as they should
#

println("LHS empty string: " . iff("" iswm "aaaa", "true", "false"));

println("case 1: " . iff("test *" iswm "test", "true", "false")); # false
println("case 2: " . iff("test*abcdefghijk*" iswm "test", "true", "false")); # false
println("case 3: " . iff("test*" iswm "test", "true", "false")); # true
println("case 4: " . iff("test******************" iswm "test", "true", "false")); # true
println("case 5: " . iff("test**********?" iswm "test", "true", "false")); # false
println("case 6: " . iff("test***?****" iswm "test", "true", "false")); # false
println("case 7: " . iff("test****?****" iswm "test", "true", "false")); # false
println("case 8: " . iff("test?*" iswm "test", "true", "false")); # false
println("case 9: " . iff("test*?*" iswm "test", "true", "false")); # false
println("case 10: " . iff("test?" iswm "test", "true", "false")); # false

println("case a: " . iff("*test" iswm "test", "true", "false")); # true
println("case b: " . iff("?test" iswm "test", "true", "false")); # false
println("case c: " . iff("**test" iswm "test", "true", "false")); # true
println("case d: " . iff("*?*test" iswm "test", "true", "false")); # false

println("case e: " . iff("tes*t" iswm "test", "true", "false")); # true
println("case f: " . iff("tes?t" iswm "test", "true", "false")); # false
println("case g: " . iff("tes*?*t" iswm "test", "true", "false")); # false
println("case h: " . iff("tes**********t" iswm "test", "true", "false")); # true
