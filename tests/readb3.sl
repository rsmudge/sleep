debug(3);

$handle = fork({
   for ($x = 0; $x < 1000; $x++)
   {
      writeb($source, ".");
   }
});

$data = readb($handle, -1);
$count = strlen($data);

println("Read $count bytes");
