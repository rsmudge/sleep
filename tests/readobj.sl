
debug(7);
global('$handle $temp');

$handle = allocate();
writeObject($handle, "this is a test");
writeObject($handle, 1);
writeObject($handle, @("a", "b", @("c", "d", "e")));
writeObject($handle, { println("Hello World!"); });
closef($handle);

while $temp (readObject($handle))
{
   if (!-isfunction $temp)
   {
      println("Read: $temp");
   }
   else
   {
      println("Read: $temp (function, will invoke!)");
      invoke($temp);
   }
}
