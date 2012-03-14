#
# oops, I had broken the xor operatopr
#

println("test1: " . iff((2L ^ 2L) == 0, "pass", "fail"));
println("test2: " . iff((4L ^ 2L) != 0, "pass", "fail"));
println("test3: " . iff((2 ^ 2) == 0, "pass", "fail"));
println("test4: " . iff((4 ^ 2) != 0, "pass", "fail"));
println("test5: " . iff((4 ^ 2) == 6, "pass", "fail"));
println("test6: " . iff((4L ^ 2L) == 6, "pass", "fail"));
