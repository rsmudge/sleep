#
# show lambda creating shared environments...
#

sub foo
{
   println("Value: $bar");
}

let(&foo, $bar => "example 1");
foo();

[let(&foo, $bar => "example 2")];
foo();
