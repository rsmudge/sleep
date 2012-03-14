#
# Quick Script to test some regex stuff...
#

if ("abcde" ismatch '\w++')
{
   printf("Simple match works...");
}

if ("butane!lamer@aol" ismatch '.*!.*@.*')
{  
    printf("Address matching works...");
}

if ("false@aol.com" ismatch '.*!.*@.*')
{
    printf("I should not trigger...");
}

$nick = '([\-|\[|\]|\\|\`|\^|\{|\}|\w]++)';
$user = '([\~\w]++)';
$host = '(\w++[[\.|\:]\w++]*)';     

if ('`butane!root@irc.aol.com' ismatch "$nick $+ !( $+ $user $+ @ $+ $host $+ )")
{
   printf("Rock on... complicated regex works...");
}

foreach $var (matches('`butane!root@irc.aol.com', "$nick $+ !( $+ $user $+ @ $+ $host $+ )"))
{
   printf("Breaking it down: $var");
}

foreach $var (split(" ", "this is a test"))
{
   printf("Breakdown2: $var");
} 

printf("Quick test: " . join('EQUALS', split("=", "key=value")));

printf("Replace: " . replace("`butane is the man", " is ", " was "));
