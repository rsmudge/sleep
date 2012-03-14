@a = @("a", "b", "c", "d");

foreach $index => $value (@a)
{
   println("$index => $value");
   if ($value eq "c")
   {
      push(@a, "d");
   }
}   
