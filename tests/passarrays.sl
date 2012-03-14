sub blah
{
   local('@data');
   @data = $1;

   foreach $var (@data) {
     println("Hrm.. value is : $var");
   }
}

blah(array("this", "is", 'a', "test", "with", 7, "words"));
println("This should be empty: " . @data);
