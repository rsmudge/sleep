#
# test arrays...
#
@temp = array("a", "b", "c", "d", "e", "f");
printAll(@temp);
println("---");

foreach $index => $value (@temp)
{
   println("@temp[ $+ $index $+ ] = $value : " . @temp[$index]);

   if ($value eq "c" || $value eq "e") 
   {
       remove();
   }
}

println("---");
printAll(@temp);

# test out the error clause...
remove();

