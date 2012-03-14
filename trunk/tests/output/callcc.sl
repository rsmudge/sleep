Trace: &global('$value') at callcc.sl:18
foo start: &closure[callcc.sl:9-15]#1
Trace: &println('foo start: &closure[callcc.sl:9-15]#1') at callcc.sl:9
Trace: &foo() -goto- &closure[callcc.sl:11-13]#2 at callcc.sl:19
Hello Continuation: &closure[callcc.sl:9-15]#1
Trace: &println('Hello Continuation: &closure[callcc.sl:9-15]#1') at callcc.sl:11
foo postcc - from anonymous function!
Trace: &println('foo postcc - from anonymous function!') at callcc.sl:15
Trace: [&closure[callcc.sl:9-15]#1: 'from anonymous function!'] at callcc.sl:12
Trace: [&closure[callcc.sl:11-13]#2 CALLCC: &closure[callcc.sl:9-15]#1] = 'this is foo's new value' at callcc.sl:10
Does this ever happen? this is foo's new value
Trace: &println('Does this ever happen? this is foo's new value') at callcc.sl:20
Trace: &debug(7) = 7 at callcc.sl:22
this is bar: 3456
Continuation &closure[callcc.sl:38-43]#3 saved to buffer...
Bar is done :)
I am alive from the dead... 3456
3456
