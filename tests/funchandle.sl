#
# This is a test for a problem that existed in Sleep 2.0-b1 => b6.  Basically any string beginning with
# an & could be interpreted as a function handle in the right context.
#
# However since HOES and closure calls are an overloaded syntax, when one specifies '&some string' what is
# it?  A HOES call on a string object or a closure call on an existing function.
#
# Kind of a scary little hole as a scripter could inadvertently be using HOES to play with user input and
# accidentlaly give the user the possibility of calling any &function within the sleep engine.
#
# This has been fixed!!!!   
#

sub dangerous
{
   # imagine if this was a function to format your hard drive?!?

   println("The dangerous function was called: $1");
   return "";
} 

$test = &dangerous;


[&dangerous: "this is safe and allowable"];
[$test: "this is also safe and allowable"];

println("Test: " . ['&dangerous' substring: 1, 7]);

