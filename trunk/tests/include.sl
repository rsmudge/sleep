#
# test of the include function...
#

debug(7);

global('$GLOBAL_VAR');

$GLOBAL_VAR = "Harf... bish";

sub foo
{
   println("This is foo() - :)");
}

# case 1:
include("data/scripts.jar", "scripts/injar.sl");

debug(7);
println("Eh?!? $INJAR_VAR");

# case 2:

[{ include("data/scripts.jar", "scripts/errors1.sl"); }];

# case 3:
include("data/scripts.jar", "scripts/does_not_exist.sl");
