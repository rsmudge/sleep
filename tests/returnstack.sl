sub test
{  
   return "test";
} 

sub test2
{
   test();
   return;
}

println(test());
println(test2()); # echo should be blank

