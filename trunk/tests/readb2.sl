$handle = openf("readb2.sl");
println(available($handle) . " vs. " . lof("readb2.sl"));
$data = readb($handle, -1);
closef($handle);

println($data);
