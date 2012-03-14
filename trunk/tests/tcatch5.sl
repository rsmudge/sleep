debug(2);

import java.util.*;

sub object
{
   if ($0 !is $null)
   {
      if (-isfunction $this[$0])
      {
         return invoke($this[$0], @_, $this => $this);
      }
      else
      {
         return $this[$0];
      }
   }
}

#
# mmm, sleep objects :)
#
sub foo
{
   local('$enumeration');
   $enumeration = lambda(&object);
   $enumeration['@data'] = @("a", "b", "c", "d", "e");
   $enumeration["hasMoreElements"] = { return 1; };
   $enumeration["nextElement"] = { 
      if (size(@data) > 0)
      {
         return pop(@data);
      }
      else
      {
         throw [new NoSuchElementException: "overextending my bounds dude :("];
      }
   };

   return $enumeration;
}

try
{
   println([Collections list: foo()]);
}
catch $ex
{
   println("Error: ".[$ex getMessage]." from: ".[$ex getClass]);
   printAll(getStackTrace());
}

try
{
   println("Trying again... what will java do?");
   println([Collections list: {
      throw "haha... testing bish!@#$";
   }]);
}
catch $ex
{
   println("Error: ".[$ex getMessage]." from: ".[$ex getClass]);
   printAll(getStackTrace());
}

