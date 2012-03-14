#
#
#
debug(24);
println("uNF... basic cas3 baby!@%");

sub foo
{
   $x = 1 + 2;
   println("In the sluts() function...");
   if ($x == 3)
   {
      bar();
   }
   println("more stuff...");
}

sub bar
{
   $x = 3 + 4;
   throw "hehe... from bar baby!@#$";
   println("Bar stuff wheee...");
}

try
{
   $xyz = "nothing wrong here, I promise!@#";
   println($xyz);
   $mmm = { foo(); };
   [$mmm test: "this is a HOES call"];
   println("More after foo");
}
catch $exception
{
   println("An exception was thrown... $exception");
   printAll(getStackTrace()); # will return something...
}

println("The beat does go on...");
