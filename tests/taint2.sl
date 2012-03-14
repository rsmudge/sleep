debug(debug() | 128);

sub foo
{
   return @("a", "b", $1);
}

@v = foo(@ARGV[0]);

println(eval(@v[2]));
