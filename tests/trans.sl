#
# Test of translation tools...
#

# rot13 test

$cipher = tr("this is a test uNF 12345!!!!", 'a-zA-Z', 'n-za-mN-ZA-M');
println("Cipher text: " . $cipher);

$plain = tr($cipher, 'a-zA-Z', 'n-za-mN-ZA-M');
println("Plain  text: " . $plain);

# swap test

println("Swap: " . tr("AAA BABA BBB", "AB", "BA"));

# delete all numbers

println("Numbers? " . tr("Th1s 1s 4 t3st", '\d', ""));

# complement...

println("Complement? " . tr("Th1s 1s 4 t3st", '\d', "", 'c'));

# squeeze

println("Squeeze: " . tr("AAA BABA BBB", "AB", "BA", 's'));

