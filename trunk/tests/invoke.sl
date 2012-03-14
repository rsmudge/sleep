sub test
{
   if ($0 is $null)
   {
      println("&test - no \$0 - but $1 $2 and $3");
   }
   else
   {
      println("I am a subroutine with 1: $1 2: $2 and 3: $3 told $0");
   }
}

invoke(&test, @("a", "b", 3), "testing");
invoke(&test, @("aa", "bb", 3));

