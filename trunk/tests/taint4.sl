debug(debug() | 128);

$handle = openf("taint4.sl");
@d = readAll($handle);
closef($handle);

$data = join("", @d);
eval($data);
