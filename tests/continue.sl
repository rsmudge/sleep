sub test1
{
   for ($x = 0; $x < 100; $x++)
   {
      if ($x > 10 && $x < 90)
      {
         continue;
      }

      println($x);
   }

   println("Done!@#$");
}

sub test2
{
   for ($x = 0; $x < 100; $x++)
   {
      println("X:    $x");
      for ($y = 0; $y < 100; $y++)
      {
          if ($y > 10 && $y < 90)
          {
             continue;
          }

          println("   Y: $y");
      }

      if ($x == 2)
      {
          if (3 == 3) # just testing, nesting the break several blocks deep, used to break only out of the current loop!
          {
             break;
          }
      }
   }

   println("Done Again!@#$");
}

sub test3
{
   @temp = array("a", "b", "c", "d", "e", "f", "g", "h");
   foreach $var (@temp)
   {
      if ($var eq "c" || $var eq "d")
      {
         println("Skipping: $var");
         continue;
      }

      println($var);
   }


   println("Done for the third time...");
}

test1();
test2();
test3();
