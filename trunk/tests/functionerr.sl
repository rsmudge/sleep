sub foo
{
   println("hello world!");
}

$x = function('&foo');
println($x);

$x = function('foo');
println($x);
