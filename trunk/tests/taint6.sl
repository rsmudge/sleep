debug(debug() | 128);

$x = @ARGV[0];

while ($x hasmatch '([^ ]+)')
{
   $z .= matched()[0];
}

