debug(7);

global('$hash');

sub tryit
{
   setRemovalPolicy($hash, { return iff(size($1) >= 4); });

   add($hash, a => "apple", b => "boy", c => "cat");
   println($hash);

   add($hash, d => "dog");
   println($hash);

   println("Access 'a' " . $hash["a"]);
   println($hash);

   add($hash, e => "emu");
   println($hash);

   add($hash, m => "night elf mohawk");
   println($hash);

   println("Remove 'm'");
   $hash["m"] = $null;
   println($hash);

   add($hash, n => "nerf");
   println($hash);

   add($hash, x => "XXX");
   println($hash);
}

println("Insertion Ordered Hash ----------");
tryit($hash => ohash());
println("Access Ordered Hash    ----------");
tryit($hash => ohasha());
