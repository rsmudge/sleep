#
# making sure parsing of floating point numbers isn't jacked up with altered concat operator
#

println("This number is" . 1.2);
println("Math: " . (3 * 4.5));

println("Long: " . 4847324738247832L);

println("Hex:  " . 0x1F);
