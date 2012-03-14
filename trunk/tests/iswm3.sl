sub foo 
{
   if ($1 iswm $2)
   {
      println("true");
   }
   else
   {
      println("false");
   }

   println("a: $1 b: $2");
}

$a = "**at\\\\est";
$b = "bat\\est";
foo($a, $b);

$a = "*at\\\\est";
$b = "bat\\est";
foo($a, $b);

$a = "?at\\\\est";
$b = "bat\\est";
foo($a, $b);

$a = "?at\\\\est\\\\";
$b = "bat\\est\\";
foo($a, $b);

# fringe case, this comes up true which is ok with me... \ followed by nothing
$a = "?at\\\\est\\";
$b = "bat\\est\\";
foo($a, $b);

$a = "?at\\\\est\\aa";
$b = "bat\\est\\aa";
foo($a, $b);

$a = "?at\\\\est\\\\aa";
$b = "bat\\est\\aa";
foo($a, $b);


