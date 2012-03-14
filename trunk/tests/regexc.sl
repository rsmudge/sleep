#
# checking that a regex state travels with a closure / coroutine...
#

sub func1
{
   if ("raffi@hick.org" ismatch '(.*?)\@(.*?)')
   {
      yield "the waiting is the hardest part...";
      println(matched());
   }
}

sub func2
{
   if ("rmudge@syr.edu" ismatch '(.*?)\@(.*?)')
   {
      yield "the waiting is the hardest part...";
      println(matched());
   }
}

func1();
func2();
func1();
func2();

