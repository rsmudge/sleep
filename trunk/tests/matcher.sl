$my_th = {
   $mystr = "[12324234] AAA BBBAAA AAA";

   if ($mystr hasmatch '\[(\d+)\].+') 
   {
      println("CORRECT");
   }
   else
   {
      println("INCORRECT!");
   }
};

while($ii < 10) 
{
  $ii = $ii + 1;
  fork($my_th, $iter => "$ii");
}

sleep(3000);
