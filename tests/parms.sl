#
# Testing parameter passage... make sure all is cool here
#

sub func1
{
   printf("I'm func 1 - I've got 3 parms $1 $+ , $2 $+ , and $3");
   func2("blitzen", "donner", "dasher");
   printf("I'm func 1 - I've still got 3 parms $1 $+ , $2 $+ , and $3");
}

sub func2
{
   printf("I'm func 2 - I've got 3 parms $1 $+ , $2 $+ , and $3");
}

func1("sleepy", "dopey", "happy");
