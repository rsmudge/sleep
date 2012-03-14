#
# test setting of fields...
#

import org.hick.blah.SqueezeBox from: data/test.jar;

$sb1 = [new SqueezeBox];
$sb2 = [new SqueezeBox];

println("Pre change:");
[$sb1 printValues];

setField(^SqueezeBox, aDoubleField => 4);
[$sb1 printValues];
[$sb2 printValues];

println("-" x 60);

setField($sb1, instanceStringField => "squeeze box 1");
setField($sb2, instanceStringField => "squeeze box 2");
[$sb1 printValues];
[$sb2 printValues];

println("-" x 60);

setField($sb1, instanceBooleanField => 0);
[$sb1 printValues];
[$sb2 printValues];

println("-" x 60);

setField($sb2, aStringField => "the rain in spain, falls mainly on the plain");
[$sb1 printValues];
[$sb2 printValues];

println("-" x 60);

setField($sb1, instanceBooleanField => 1, instanceStringField => "this is another test", aDoubleField => 5.0);
[$sb1 printValues];
[$sb2 printValues];

println("-" x 60);

[{
   setField($sb2, nofield => 33);
}];

[{
   setField($sb2, instanceBooleanField => "false and true?");
}];

println([$sb1 squeeze]);
setField($sb1, sq => 55);
println([$sb1 squeeze]);

println("Done?");
