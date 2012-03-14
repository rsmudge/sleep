#
# A quick test of the use() function 
#

# assume we're running this from the toplevel sleep directory.

use("./data/test.jar", "org.hick.tests.TestLoadable");

if (checkError($error))
{
   println("Error loading jar: $error");
}

foo();
foo("a", "b", "c");

println("Testing: " + foo());
