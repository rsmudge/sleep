#
# coroutine test 
#    ensure that coroutine state is not lost when another coroutine is called within the same coroutine
#    note that this test is *different* from a coroutine being able to recursively call itself... 
#  

sub a
{
   this('$y');  
   $y = 0;
   while ($y < 10)
   {
      println("a() yields: $y * b()");
      yield $y * b();
      $y++;
   }

   return -1;
}

sub b
{
   this('$x');
   $x = 10;
   while ($x < 20)
   {
      println("b() yields: $x * c()");
      yield $x * c();
      $x++;
   }
}

sub c
{
   this('$z');
   $z = 20;
   while ($z < 30)
   {
      println("c() yields: $z");
      yield $z;
      $z++;
   }  
}

$z = a();
while ($z > -1)
{
   println($z);
   $z = a();
} 
