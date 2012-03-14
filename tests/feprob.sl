%h = %(a => "apple", b => "bat", c => "cat");

%h["b"] = $null;

foreach $key => $value (%h)
{
   println("$key and $value");
}
