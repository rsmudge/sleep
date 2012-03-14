#
# some fun serializing closure metadata...
#

debug(7 | 34);

global('$buffer $ex');
$buffer = allocate();

sub foo
{
   if ($1 ismatch '.*')
   {
   }
   yield;
}

foo("blah");
writeObject($buffer, &foo);

sub bar
{
   local('$iter');

   foreach $iter (@(1, 2, 3, 4, 5, 6, 7))
   {
      yield;
   }
}

bar();
writeObject($buffer, &bar);
