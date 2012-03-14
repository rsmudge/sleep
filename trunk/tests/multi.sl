# 
# Test of data structure robustness with multiple levels of stuff...
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

@data = array('item 1', 
              'item 2',  
               array(
                  'sub array 1',
                  'sub array 2',
                  'sub array 3',
                  'sub array 4',
               ),
               'item 4',
               'item 5');

recurse(@data);

#
# Hash of Hashes / Scalars
#

sub test
{
   return "kvp op takes functions";
}

%data2 = hash(key1 => "value 1",
              key2 => "value 2",
              key3 => test(), 
              %key3 => hash(
                  key3a => "value a",
                  key3b => "value b",
                  key3c => "value c",
                  key3d => "value d"),
              key4 => "value 4");

recurse(%data2);

#
# Hash of Arrays
#
%data3 = hash(letters => array("a", "b", "c"),
              numbers => array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
              puncs   => array("!", "@", "%", ".", ",", "^"),
              letters+numbers+puncs => array(
                  array("a", "b", "c"),
                  array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                  array("!", "@", "%", ".", ",", "^")
              ),
              astring => "this is just a string for good measure");

recurse(%data3);

#
# Array of Hashes
#

@data4 = array(hash(rsmudge => "Raphael Mudge", fvmudge => "Frances Mudge", bjmudge => "Brad Mudge"),
               hash(olopez  => "Orlando Lopez", hlopez  => "Heather Lopez"),
               hash(jsmith  => "John Smith", jjsmith => "Jimmy John Smith", rj => "Rick James!!!")
              );

recurse(@data4);
