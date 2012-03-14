$string = "this is a test really it is and I love it like McDonalds ketucky fried chicken and a pizza hut";
$start = 0;

while $idx (indexOf($string, 'a', $start))
{
   println("$start and $idx $+ : " . substr($string, $start, $idx + 1));
   $start = $idx + 1;
}

println("finish it: " . substr($string, $start));
