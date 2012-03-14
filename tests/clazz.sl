#
# class literals...
#

println(^System);
println(^LinkedList);
println(^java.util.LinkedList);
println(^java.util.LinkedList."this is a string :)");
println(^java.util.LinkedList."this is a string :)");
println(^java.util.LinkedList."this is a string :)");
println(^java.lang.Character$Subset."another string...");

$data = "HEllo World";

println(^java.lang.Character$Subset.$data);

$var = ^java.lang.Character$Subset x 4;
println($var);

expr('^java.lang.Characte$Subset');
println([checkError() formatErrors]);

printAll([^Map getMethods]);
