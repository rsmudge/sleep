%data = ohasha(a => "apple", b => "bat", c => "cat", d => "dog");

println(%data["c"]);

foreach $key => $value (%data)
{
   println("$key and $value");
   if ($key eq "b")
   {
      remove();
   }
}

println(%data);
