# The Sleep Console

The Sleep console is just a quick and dirty interface for interacting with
the sleep library.  Sleep includes API's for integrating the console with 
your application.   Personally I just use the console for debugging the 
language.  

## To Launch the Sleep Console

[raffi@beardsley ~/sleep/bin]$ java -jar sleep.jar

To launch a script from the command line:

java -jar sleep.jar filename.sl

The console class sleep.console.TextConsole is a small class for using the 
console as a command line application.  If you want an example of 
integrating the console into your application read the source code for the 
sleep.console.TextConsole.

## Console Commands

debug [script] <level>
   Sets the default debug level or sets the debug level for the specified 
   script.  Debug levels include:
   1 - show all errors
   2 - show all warnings normally available with checkError()
   4 - flag a warning when an undeclared variable is used for the first time
   8 - trace all function calls

env [functions/other] [regex filter]
   Dumps the shared environment.  This includes all functions, operators, and
   predicates and where they are registered too.  The regex filter allows one
   to specify a regular expression to filter the results with.

help
   Displays the help message which offers a short summary of each command.

interact
   Enters the console into interactive mode.  From here full blocks of sleep
   code can be typed in.  Once one wants to evaluate the typed in code they
   simply type '.' and the code will be evaluated and executed.  Subsequent 
   uses of '.' will repeat the previous code sequence.   Any syntax errors 
   will be reported back.  Typing 'done' or Ctrl+D will effectively stop 
   interactive mode.

list
   Lists all of the currently loaded scripts

load <file>
   Loads a script file.

unload <file>
   Unloads a script file.

tree [key]
   Displays the Abstract Syntax Tree for the specified key.  This key can be
   either a script name or a function reference.  If no key is specified then
   the expression evaluated last is used.

quit
   Exits the console.

x <expression>
   Evaluates a sleep expression and displays the value.

? <predicate expression>
   Evaluates a sleep predicate expression and displays the truth value.
