sub test
{
   if (!($1 - 10))
   {
      println("I rule... you drool: $1");
   }

   if (!$1)
   {
      println("Parameter $1 is negated");
   }
}

test(34);
test();
test(10);
