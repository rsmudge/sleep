Trace: &global('$INJAR_VAR') at injar.sl:3
This is a script included from a jar file
Trace: &println('This is a script included from a jar file') at injar.sl:6
This is foo() - :)
Trace: &println('This is foo() - :)') at include.sl:13
Trace: &foo() at injar.sl:9
Done with injar.sl -- Harf... bish
Trace: &println('Done with injar.sl -- Harf... bish') at injar.sl:12
Trace: &substr('test', 8, 20) - FAILED! at injar.sl:17
Warning: &substr: illegal substring('test', 8 -> 8, 20 -> 4) indices at injar.sl:17
Trace: &debug(7) = 7 at include.sl:19
Eh?!? Hello from injar.sl
Warning: checkError(): YourCodeSucksException: 3 error(s): Mismatched Parentheses - missing close paren at 9; Mismatched Braces - missing close brace at 6; Runaway string at 9 at include.sl:24
Warning: checkError(): java.io.IOException: unable to locate scripts/does_not_exist.sl from: data/scripts.jar at include.sl:27
