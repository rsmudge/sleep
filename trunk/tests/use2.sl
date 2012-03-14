#
# A quick test of the use() function 
#

# assume we're running this from the toplevel sleep directory.

import org.hick.tests.* from: "./data/test.jar";

use(^TestLoadable);

foo();
foo("a", "b", "c");

println("Testing: " + foo());
