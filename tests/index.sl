# 
# Test of data structure robustness with multiple accesses :)
#

sub recurse
{
   if (-isarray $1)
   {
      recurseA($1);
   }
   else if (-ishash $1)
   {
      recurseH($1);
   }
}

sub recurseA
{
   local('$var');

   foreach $var ($1)
   {
      if (-isarray $var)
      {
         recurseA($var, "$2  ");
      }
      else if (-ishash $var)
      {
         recurseH($var, "$2  ");
      }
      else
      {
         println("$2 $var");
      }
   }
}

sub recurseH
{
   local('$key %hash $value');
   %hash = $1;

   foreach $key (keys(%hash))
   {
      $value = %hash[$key];

      if (-ishash $value)
      {
         println("$2 $key is:");
         recurseH($value, "$2  ");
      }
      else if (-isarray $value)
      {
         println("$2 $key is");
         recurseA($value, "$2  ");
      }
      else
      {
         println("$2 $key => $value");
      }
   }
}

#
# Array of Arrays / Scalars
#

@data[0]    = 'item 1';
@data[1]    = 'item 2';
@data[2][0] = 'sub array 1';
@data[2][1] = 'sub array 2';
@data[2][2] = 'sub array 3';
@data[2][3] = 'sub array 4';
@data[2][4][0] = 'sub sub array a';
@data[2][4][1] = 'sub sub array b';
@data[2][4][2] = 'sub sub array c';
@data[2][4][3] = 'sub sub array d';
@data[2][4][4] = 'sub sub array e';
@data[2][4][5] = 'sub sub array f';
#@data[2]    = array('sub array 1', 'sub array 2', 'sub array 3', 'sub array 4');
@data[3]    = 'item 4';
@data[4]    = 'item 5';

recurse(@data);

#
# Hash of Hashes / Scalars
#
%hash["a"]   = "item a";
%hash["b"]   = "item b";
%hash["c"]   = "item c";
%hash["d"]   = "item d";
%hash["e"]   = "item e";
%hash[1]   = "item 1";
%hash[2]   = "item 2";
%hash[3]   = "item 3";
%hash[4]   = "item 4";
%hash[5]   = "item 5";
%hash["numbers"][6] = "number 6";
%hash["numbers"][7] = "number 7";
%hash["numbers"][8] = "number 8";
%hash["numbers"][9] = "number 9";

recurse(%hash);
