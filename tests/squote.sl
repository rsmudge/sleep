#
# Single Quote Behavior...
#


println('\''); # single quoted single quote '

println('\\'); # a double backslash... plz... :) for regex sakes \\

println('\\\'\\'); 

println('\\\\\\\\\\\\\\\\');

println('\a\b\c\d\e\f\g\h\i\j\k\l\m\n\o\p\q\r\s\t\u\v\w\x\y\z');

eval("
    println('\\\\'');
");

if (checkError($error))
{
   println("Error running eval'd code: $error");
}
