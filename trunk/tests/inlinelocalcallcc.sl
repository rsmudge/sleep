# test or set in stone how Sleep handles
# restoration of local variables from a callcc
# within an inline function... dun dun dun.

inline foo
{
   println("Welcome to foo: " . @_);

   callcc
   {
      println("This is within callcc: $1 and " . @_);
      [$1: "c", "d"];
   };

   println("What fun fruit rollups will callcc roll out with next? " . @_ . " $1 and $2");
}

sub bar
{
   println("In the bar: " . @_);
   foo("a", "b");
   println("Out of the bar: " . @_ . " $1 and $2");
}

bar("toplevel", "argument");
