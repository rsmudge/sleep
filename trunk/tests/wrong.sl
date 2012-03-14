#
# this should print out as 81.0

debug(15);

[[System out] println: [Math pow: 3, 4]];

# boolean test
[[System out] println: casti(1, "z")];
[[System out] println: casti(0, "z")];

# double test
[[System out] println: casti(1, "d")];

# byte test
[[System out] println: casti(1, "b")];

# float test
[[System out] println: casti(100, "f")];

# char test
[[System out] println: casti("b", "c")];

# int test
[[System out] println: casti(63, "i")];

# string test
[[System out] println: "this is a string y0"];

# object test
[[System out] println: [new StringBuilder: "test"]];

# char array test
[[System out] println: cast(@("a", "b", "c", "d"), "c")];
