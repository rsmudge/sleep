#
# foreach iterators and regex bridge use a metadata hash to keep track of context sensitive data
# this test assures that foreach loop handles recursion 

global('$i');

sub foo
{
   local('$var');
   println("$i Enter foo!");

   foreach $var ($1)
   {
      if ($var eq "icecream" && $2 is $null)
      {
         println("$i Calling next foo thingie");
         $i = "  ";
         foo($1, 1);
         $i = "";
      }
      println("$i $var");
   }

   println("$i Exit foo!");
}

foo(@(1, 2, 3, "icecream", 4, 5, 6, 7));

