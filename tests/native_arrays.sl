#
# the final Jedi test...
#

@a = array('3.0', '4.0', '5.0', '6.0');
@b = array('3.0', '4.0', '5.0', '6.0');

if ([Arrays equals: @a, @b])
{
   println("A: The two arrays are equal...");
}

# test 2...  ensure everything is autocast to a double

@a = array(3.0, 4.0, 5.0, 6.0, 7.0);
@b = array(3.0, 4.0, 5.0, 6, "7");

if ([Arrays equals: @a, @b])
{
   println("B: The two arrays are equal...");
}

#
# another test... just making sure everything is autocast to strings
#

$x = [new StringBuffer: "testing"];

@a = array('3.0', '4.0', 'hjdsjkhd', 6.0, $x);
@b = array('3.0', '4.0', 'hjdsjkhd', '6.0', $x);

if ([Arrays equals: @a, @b])
{
   println("C: The two arrays are equal...");
}

#
# test inequality to make sure my tests aren't BS'ing me :)
#

$x = [new StringBuffer: "testing"];

@a = array('3.0', '4.0', 'hjdsjkhd', 6.0, $null);
@b = array('3.0', '4.0', 'hjdsjkhd', '6.0', $x);

if (![Arrays equals: @a, @b]) # see the exclamation point, phear the exclamation point
{
   println("D: The two arrays are not equal...");
}

#
# Testing the cast function...
#

@a = array("sluts", "whores", "skanks", "harlets", "any ex-girlfriend", "hoe", "garden tool");
@a = sorta(@a); # oops gotta make sure its sorted...

println("Lets do a search: " . [Arrays binarySearch: cast(@a), "skanks"]);

#
# another simple cast...
#
@b = array(1, 2, 3, 4, 5, "6", "78904565", 3345L, 9.5);
@b = sortn(@b);

println("Another binary search: " . [Arrays binarySearch: cast(@b, "i"), 3345]);

import java.lang.reflect.*;

#
# a multidimensional cast... :)
#
@c = array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

$temp = cast(@c, "i", 6, 2); # create a 2x2x3 array

#
# a multidimensional cast... :)
#
$temp = cast(@c, "i", 2, 2, 3); # create a 2x2x3 array

println('<--------------------------------------------------->');

for ($x = 0; $x < 2; $x++)
{
   $array = [Array get: $temp, $x]; # this is all we need because Sleep will auto convert any HOES function
                                    # returning an array into a Sleep array of however many dimensions

   for ($y = 0; $y < 2; $y++)
   {
      for ($z = 0; $z < 3; $z++)
      {
         println('$temp[' . $x . '][' . $y . '][' . $z . '] = ' . $array[$y][$z]);
      }
   }
}

$temp = cast(@c, "i", 2, 6); # create a 2x6 array

println('<--------------------------------------------------->');

for ($x = 0; $x < 2; $x++)
{
   $array = [Array get: $temp, $x];

   println("We have $array and " . [$array getClass]);

   for ($y = 0; $y < 6; $y++)
   {
      println('$temp[' . $x . '][' . $y . '] = ' . $array[$y]);
   }
}

if (1) # testing jumping out of a block after an error occurs...
{
   $temp = cast(@c, "i", 3, 6); # test the warning that gets fired...
   println("I never execute");
}

println("Still going...");

println('<--------------------------------------------------->');

$temp = cast("this is a test", "c"); # cast the string into a character array...

for ($x = 0; $x < [Array getLength: $temp]; $x++)
{
   println("temp[ $+ $x $+ ] = " . [Array get: $temp, $x]);
}

println('<--------------------------------------------------->');

$temp = cast(pack('iiZi', 32, 1, "testing", 2), "b"); # cast the string into a byte array..

for ($x = 0; $x < [Array getLength: $temp]; $x++)
{
   println("temp[ $+ $x $+ ] = " . [Array get: $temp, $x]);
}

