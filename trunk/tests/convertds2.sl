import sleep.* from: data2/test.jar;

$a = [new ArrayTest1];
[$a foo: @("x", 1, 2, 3, "str")]; # array of strings
[$a foo: @(1, 2, 3, "str")];      # array of integers
[$a foo: @(1.0, 2, 3, "str")];    # array of doubles
[$a foo: @(1L, 2, 3, "str")];     # array of longs
[$a foo: cast(@(1L, 2, 3, "str"), "z")];     # array of booleans anyone?
[$a foo: cast(@(1L, 2, 3, "str"), "f")];     # array of floats
[$a foo: cast(@(1L, 2, 3, "str"), "o")];     # array of objects 

[$a foo: @([new HashSet])]; # array of objects?
