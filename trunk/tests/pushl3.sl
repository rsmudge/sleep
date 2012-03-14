#
# test for scope management...
#

inline bar
{
   pushl();
   popl();
   popl();
}

sub foo
{
   bar();
}

foo();
