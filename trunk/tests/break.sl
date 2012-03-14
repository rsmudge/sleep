sub test1
{
   for ($x = 0; $x < 100; $x++)
   {
      println($x);
      if ($x == 10)
      {
         break;
      }
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
          println("   Y: $y");

          if ($y == 10)
          {
             break;
          }
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
      println($var);
      if ($var eq "c")
      {
         println("Breaking!@");
         break;
      }
   }


   println("Done for the third time...");
}

test3();
test1();
test2();
