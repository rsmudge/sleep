#
# preventing block skew... maybe?!?
#

$x = {
  println("this is a test");
  if (1 == 2)
  {
     println("this won't ever happen");
  }
};

$y = {
  println("this is a test");
  while (1 == 2)
  {
     println("this won't ever happen");
  }
};

$z = {
  println("this is a test");
  if (1 == 2)
  {
     println("this won't ever happen");
  }
  else
  {
     println("this will though");
  }
};

println($x);
println($y);
println($z);

