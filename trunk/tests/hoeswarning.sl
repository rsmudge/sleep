#
# Make sure HOES is catching java exceptions correctly...
#
$long  = [Long    valueOf: "4782378478324474fjjsdfjsdjkfjk3"];

if (checkError($error))
{
   println("Trouble converting long: $error from: " . [$error getClass]);
}

#
# make sure bad field is being caught...
#

$long = [Long tHiS_FiElD_DoES_nOT_EXIsT];

#
# make sure argument mismatch is caught...
#

$long = [Long valueOf: 3, 4, 5, 6, 7, 8, 9, 10];

#
# test no such constructor message...
#

$obj = [new StringTokenizer];

#
# test illegal access (by trying to instantiate an abstract class)
# 

$model = [new javax.swing.AbstractListModel];
