#
# some tests on connected pipes...
# 

$handle = fork({
   while $text (readln($source))
   {
      println("A: read $text");
   }

   println("A: detected EOF!!!");
   return "done!";
});

fork({
   println($handle, 1);
   println($handle, 2);
   println($handle, 3);
   printEOF($handle);

}, \$handle);

println("Done, received: " . wait($handle));
