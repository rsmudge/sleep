inline a
{
   println("@_: " . @_ . " and $1 and $2");
}

inline b
{
   println("@_: " . @_ . " and $x and $y");
}

sub foo
{
   a("apple", "bannana", "cow");
   b($x => "x-ray", $y => "yak");
}

sub bar
{
   b($x => "x-ray", $y => "yak");
   b($x => "XXX", $y => "YYY");
   println("@_ is: " . @_ . " and $1 and $2");
}

foo("aa", "bb");
bar("aaa", "bbb");

