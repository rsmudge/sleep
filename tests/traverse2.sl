@a = @("a", "b", "c", "d", "e");

while (size(@a) > 0)
{
   println("head: " . @a[0]);
   @a = sublist(@a, 1);
   println("rest: " . @a);
}
