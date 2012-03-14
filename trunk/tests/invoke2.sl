#
# updated version of &invoke :)
#

sub foo
{
   println("Message: $0");
   println("Args: " . @_);
   println("\$stuff is $stuff");
   println("\$foo is $foo");
   println("\$bar is $bar");

   $stuff = "fooified!";
   $count++;
}

sub bar
{
   this('$stuff $count');
   println($stuff);
   $stuff = "barified! $count";
}

bar();
bar();

invoke(&foo, @("a", "b", "c"), "test", $this => &bar, 
                  message => "blah", parameters => %($foo => "foo!", $bar => "bar!"));
invoke(&foo, @("x", "y", "z"), "test", $this => &bar, 
                  parameters => %($foo => "fool!", $bar => "barl!"));

bar();
foo();
bar();
