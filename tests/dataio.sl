#
# test doing a generic read with an assignment loop to suck down byte data
#

debug(7);

global('$src');

$src = fork({
  local('$str $server $x');

#  $server = listen(8888, 0);
  $server = $source;

  for ($x = 0; $x < (1024 * 10); $x++)
  {
     $str .= chr(rand(255));

     if (($x % 1024) == 0)
     {
        print(".");
     }
  }

  println();
  println("Writing a massive amount of crap");

  writeb($server, $str);
  closef($server);
});

sleep(2000);

wait(fork({
  local('$data $z');

#  $handle = connect("127.0.0.1", 8888);
#  $handle = $source; 
  
  while $data (readb($handle, 603))
  {
     println("Read: " . strlen($data) . " bytes");
     $z += strlen($data);
  }

  println("Read in $z bytes");

  closef($handle);
}, $handle => $src), 30000);
