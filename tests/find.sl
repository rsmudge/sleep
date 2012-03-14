$string = "this is some string, phear   it dude";

$start = 0;
while $index (find($string, '(\s+)', $start))
{
   $delim = strlen(matched()[0]);

   println("'" . substr($string, $start, $index + $delim) . "'");
   $start = $index + $delim;
} 

println("'" . substr($string, $start) . "'");
