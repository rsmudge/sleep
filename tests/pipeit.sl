$buffer = allocate();
println($buffer, '1234
this is a test
uNF
0r5ewr5
4567
blah
you suck
000
...');
closef($buffer);

sub myRegexHandle
{
    return fork({
        while $text (readln($handle))
        {
            if ($text ismatch $regex)
            {
                println($source, $text);
            }
        }

        closef($handle);
   }, $handle => $1, $regex => $2);
}

$h2 = myRegexHandle($buffer, '\d+'); 
while $data (readln($h2))
{
    println($data);
}
closef($h2);
