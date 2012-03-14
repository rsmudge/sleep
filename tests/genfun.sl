#
# testing out some fun with closures/arrays being semi-interchangeable
# 
debug(7); # a good habit I should try to fall into more often

global('$r1 $r2 @temp @temp2 @output');

sub generator
{
   this('$current');
   $current = $start;

   while ($current < $end)
   {
      yield $current;
      $current++;
   }

   return $null;
}

$r1 = lambda(&generator, $start => 0, $end => 100);
$r2 = lambda(&generator, $start => 5, $end => 20);

printAll($r2);

@temp = copy($r1);
println("Size of @temp is: " . size(@temp));

printAll(reverse($r2));

@temp2 = copy($r2);
println("Size of @temp2 is: ". size(@temp2));
printAll(@temp2);

println("All of the even numbered elements of @temp2 are: ");

@output = filter({ return iff(($1 % 2) == 0, $1, $null); }, $r2);
printAll(@output);
