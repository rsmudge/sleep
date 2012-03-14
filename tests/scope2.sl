global('$x');
$x = 400;

sub blah
{
   local('$x');
   $x = 3;
   println("$x");
   $x = $null;    # $x is still in local scope
   println("$x");
}

blah();
println($x);
