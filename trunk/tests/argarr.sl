#
# argument array breakage...
#

sub arr
{
   while (size(@_) > 0)
   {
      println("Another arg: " . shift(@_) . " size: " . size(@_));
   }
}

arr("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
