#
#
#

# doit()
inline doit
{
   println("doit: " . @_);

   $1 = $1 * $2;
   println($1);
}

sub foo
{
   doit(3, 4);
   println("foo: $1 and $2 and $3 and $4 etc.. and " . @_);
}

foo("phear", "u", "fear", "me");
