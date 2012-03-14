# ensure callcc works with recursion

sub a
{      
   this('$x');
   $x = 0;
   println("About to callcc a function (this one): $x");
   while ($x < 5)
   {
      callcc $this;
      println("I am continuing from here... $x");
      $x++;
   }

   return 99;    
}

println("Doing a()");
println("Rv is: " . a());




