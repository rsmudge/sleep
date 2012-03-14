#
# fork, sharing values...  
#

$counter = 90;

$a = fork({
  while ($counter < 100)
  {
     $counter++;
     println("A: $counter");
     [Thread yield];
  }

}, \$counter);

$b = fork({
  sleep(500); # I hate time kludges...

  while ($counter < 100)
  {
     $counter++;
     println("B: $counter");
     [Thread yield];
  }
}, \$counter);

wait($a);
wait($b);
