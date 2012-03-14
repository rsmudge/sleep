Outside of try block.
foo(): $c is &closure[tcatch2.sl:8]#3
1. Caught: this is being thrown because I can!!! - locals are visible (as they should be)
Post try block
Outside of try block.
foo(): $c is &closure[tcatch2.sl:8]#5
1. Caught: this is being thrown because I can!!! - locals are visible (as they should be)
0. Caught: this is being thrown because I can!!!
Outside of try block.
--- a. checkpoint 1
--- b. pre foo
foo(): $c is &closure[tcatch2.sl:8]#7
....
--- d. in exception block: this is being thrown because I can!!!
1. Caught: this is being thrown because I can!!! - 
--- e. pre throw :)
--- g. outermost catch block
0. Caught: this is being thrown because I can!!!
this outermost block isn't hosed, is it?
