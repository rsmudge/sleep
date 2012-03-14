while ('(654) 555-1212(248) 399-8219(734) 586-9912' hasmatch '\((\d\d\d)\) (\d\d\d-\d\d\d\d)')
{
   ($areaCode, $phoneNumber) = matched();
   println("$areaCode and $phoneNumber");

   while ('(123) 456-7890 sfdsdf (453) 443-3455' hasmatch '\((\d\d\d)\) (\d\d\d-\d\d\d\d)')
   {
      ($areaCode, $phoneNumber) = matched();
      println("    $areaCode and $phoneNumber");
   }
}
