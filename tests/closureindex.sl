#
# test out indexing in some other cases.
#

sub phoenetic
{
   this('$evil');

   $this["a"] = "alpha";
   $this["b"] = "bravo";
   $this["c"] = "charlie";
   $this["d"] = "delta";   

   $evil = "evil test!!!";

   println("evil test --- $evil");

   yield; 

   println("yeap, evil is now $evil");
}

phoenetic(); # set everything up please..

println("a    is: " . &phoenetic["a"]);
println("evil is: " . &phoenetic['$evil']);

&phoenetic['$evil'] = "oooh, changes... eee";
phoenetic();
