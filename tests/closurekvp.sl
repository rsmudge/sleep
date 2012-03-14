#
# ensure hashes/arrays can be passed as named parameters...
#

debug(7);

sub foo
{
   local('$var');

   foreach $var (@keys)
   {
      println("Cool: $var = " . %data[$var]);
   }
}

global('%d');
%d = %(a => "ardvark", c => "cat", b => "b4d a$$", p => "pHEAR", e => 'eAR');
foo(@keys => sorta(keys(%d)), %data => %d);
