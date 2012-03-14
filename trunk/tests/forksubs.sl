#
# test out how fork relates to subroutines
# 

sub foo
{
   println("Hello World: $this");
}

sub bar
{
   assert &foo !is $closure : "looks like we're copying closures over, bad news!";
   foo();
   [$closure];
}

wait(fork(&bar, $closure => &foo));
