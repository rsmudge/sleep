global('$p $v');

$p = "foo";
$v = "bar";

sub foo
{
   local('$p $v @data');
   @data = @("a", "b", "c", "d");
   foreach $p => $v (@data)
   {
      println("$p and $v");
   }
}  

foo();
println("$p $v");
