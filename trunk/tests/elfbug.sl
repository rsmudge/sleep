@array = array("abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vwx", "yz");

foreach $c (@array) 
{
   %hash[lc($c)] = 1;
   println("This will echo the first one only: $c");
}
