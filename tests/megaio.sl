#
# this script is similar to dataio.sl except it uses buffers to handle a larger amount of data (faster!)
# handles 10x as much data as dataio.sl in about the same amount of time.
#

debug(7);

global('$src');

$src = fork({
  local('$str $server $x');

  $server = $source;

  $str = allocate(1024 * 100);

  for ($x = 0; $x < (1024 * 100); $x++)
  {
     writeb($str, chr(rand(255)));

     if (($x % 1024) == 0)
     {
        print(".");
     }
  }

  println();
  println("Writing a massive amount of crap");

  println("Pre-Close: Avail for reading from str: " . available($str));
  closef($str);
  println("Post-Close: Avail for reading from str: " . available($str));

  writeb($server, readb($str, available($str)));
  closef($server);
});

sleep(2000);

wait(fork({
  local('$data $z');

  while $data (readb($handle, 603))
  {
     println("Read: " . strlen($data) . " bytes");
     $z += strlen($data);
  }

  println("Read in $z bytes");

  closef($handle);
}, $handle => $src), 30000);
