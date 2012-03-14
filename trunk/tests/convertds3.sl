import sleep.* from: data2/test.jar;

$a = [new ArrayTest1];

println("Primitive arrays:");
[$a bar: @(1, 2, 3, "str")];      # array of integers
[$a bar: @(1.0, 2, 3, "str")];    # array of doubles
[$a bar: @(1L, 2, 3, "str")];     # array of longs

println("Explicit conversions:");
[$a bar: cast(@(1L, 2, 3, "str"), "z")];     # array of booleans anyone?
[$a bar: cast(@(1L, 2, 3, "str"), "f")];     # array of floats

println("Should be objects:");
[$a bar: cast(@(1L, 2, 3, "str"), "o")];     # array of objects

if (1) { [$a bar: @([new HashSet], 4, 5, 6)]; } # array of objects?

println("This stuff is strings, why?");
[$a bar: @("x", 1, 2, 3, "str")]; # array of strings

println("Car tests:");
[$a car: @(1, 2, 3, "str")];      # array of integers - casts to int[]
[$a car: @(1.0, 2, 3, "str")];    # array of doubles
[$a car: @(1L, 2, 3, "str")];     # array of longs
[$a car: cast(@(1L, 2, 3, "str"), "z")];     # array of booleans anyone?
[$a car: cast(@(1L, 2, 3, "str"), "f")];     # array of floats
[$a car: cast(@(1L, 2, 3, "str"), "o")];     # array of objects
[$a car: @([new HashSet])]; # array of objects?
[$a car: @("x", 1, 2, 3, "str")]; # array of strings

println("Mar tests:");
[$a mar: @(1, 2, 3, "str")];      # array of integers - casts to int[]
[$a mar: @(1.0, 2, 3, "str")];    # array of doubles
[$a mar: @(1L, 2, 3, "str")];     # array of longs
[$a mar: cast(@(1L, 2, 3, "str"), "z")];     # array of booleans anyone?
[$a mar: cast(@(1L, 2, 3, "str"), "f")];     # array of floats
[$a mar: cast(@(1L, 2, 3, "str"), "o")];     # array of objects
[$a mar: cast(@(1L, 2, 3, "str"), "i")];     # array of integers
[$a mar: @([new HashSet])]; # array of objects?
[$a mar: @("x", 1, 2, 3, "str")]; # array of strings

println("Tar test:");
[$a tar: @(1, 2, 3)];

