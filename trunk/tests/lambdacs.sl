#
# show lambda creating shared environments...
#

sub foo
{
   this('$count');
   $count++;

   println("I am foo! $count");
}

sub bar
{
   this('$count');
   $count++;

   println("I am bar! $count");
}

$far = lambda(&foo, $this => &bar);

foo();
foo();

bar();
bar();

println("<far>");
[$far];
[$far];
println("</far>");

foo();
foo();

bar();
bar();

println("<far>");
[$far];
[$far];
println("</far>");

foo();
foo();

bar();
bar();

