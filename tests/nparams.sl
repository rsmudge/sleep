#
# a test of named parameters... fun eh?!?
#

debug(15);

sub test1
{
   local('$test');
   println("test1: $test");
}

test1($test => "success!");
test1();

sub test2
{
   println("test2: $test");
}

test2($test => "yes, another one");
test2();

sub test3
{
   this('$test');
   println("test3: $test");
   $test = ':)';
}

test3($test => 'eh?!?');
test3(); # should print nothing because $test when it was set was in the local scope, higher priority over the this scope
test3(); # should print a smiley since $test would have been in the this scope at this time

sub test4
{
   local('$count $var');

   foreach $count => $var (@_)
   {
      println("$[3]count = $var");
   }

   println("a: $a and b: $b and c: $c");
}

test4("a", "b", $a => "apple", "c", $b => "boy", "d", $c => "cat");

sub test5
{
   println("Test 5 has been called, executing action:");
   [$action];
}

test5($action => { println("The passed in closure has been called"); });
test5(action => { println("This will never happen! muah!"); });
