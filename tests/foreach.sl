foreach $var (array("a", 2, 3, 6, 7, 8, 9, 10, "blah"))
{
   println("This is a test of $var");
}

println("Aftermath: $var");

push(@temp, "blah");
push(@temp, "bleh");
push(@temp, "sluts");

println("Pre print: " . @temp);

foreach $varz (@temp)
{
   $varz = "blarg";
   println("Array " . @temp . " and $varz");
}

println("Test: " . @temp);
