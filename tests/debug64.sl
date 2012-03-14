debug(64);

$handle = fork({
   while $text (readln($source))
   {
      println("read: $text");
   }
   println("done!");
});

println($handle, "this is a test... *pHEAR*");
printEOF($handle);
wait($handle);

println("Whee... we are done now :)");
