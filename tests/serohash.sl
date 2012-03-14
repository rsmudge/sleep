%ohash = ohash();
setMissPolicy(%ohash, { warn("Miss policy called"); return 0x31337; });
setRemovalPolicy(%ohash, { warn("Removal policy called"); return 0; });

println(%ohash["a"] . ', ' . %ohash["b"]);

$buffer = allocate();
writeObject($buffer, %ohash);
closef($buffer);

$hash = readObject($buffer);
println($hash);
$hash["xx"] = "33";
println($hash);
