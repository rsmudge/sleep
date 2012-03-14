import sleep.* from: data2/test.jar;

$a = [new ArrayTest1];

[$a foo: @(casti(1L, "f"), casti(2L, "f"))]; # array of floats?
[$a foo: @(casti(1L, "h"), casti(2L, "h"))]; # array of shorts?

[$a bar: @(casti(1L, "f"), casti(2L, "f"))]; # array of floats?
[$a bar: @(casti(1L, "h"), casti(2L, "h"))]; # array of shorts?

@zz = @(casti(1L, "f"), 2, 3, "str");
filter({ $1 = casti($1, "f"); }, @zz);
[$a bar: @zz];

[$a car: @(casti(1L, "i"), 2, 3, "str")];
[$a car: @(1, 2, 3, "str")];

[$a bar: @(casti(1L, "l"), 2, 3, "str")]; 


