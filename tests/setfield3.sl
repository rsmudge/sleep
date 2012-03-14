import com.eric.* from: data3/test.jar;

$eric = [new Eric: "Eric", "Smith"];

println([$eric fname]);
println([$eric lname]);

setField($eric, fname => "Dave");

println([$eric fname]);
