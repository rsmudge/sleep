%hash = %(a => "apple", b => 'boy', c => 'cat', n => 443);

if (%hash["whatever"] is $null && %hash["rhinos"] is $null)
{
   println(%hash);
   printAll(values(%hash));
}
