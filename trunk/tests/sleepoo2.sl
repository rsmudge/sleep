#
# object oriented sleep part 2...  oooh...  
# with some creativity multiple inheritance should be possible
#

sub object
{
   if ($0 !is $null)
   {
      if ($this[$0] is $null && $parent !is $null)
      {
         if (-isfunction $parent[$0])
         {
            invoke($parent[$0], @_, $0, $this => $this);
         }
         else
         {
            return $parent[$0];
         }
      }
      else if (-isfunction $this[$0])
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
# setup our two "classes"
# 

sub class_foo 
{ 
   $this['name'] = "foo default";
}

sub class_bar 
{ 
   $this['name'] = "bar default";
}

class_foo();
class_bar();

# add a printme method to class_foo and class_bar
&class_foo["printme"] = { println("<foo> my name is: " . [$this name]); };
&class_bar["printme"] = { println("<bar> my name is: " . [$this name]); };

# add a setName method to class_foo and class_bar...
&class_foo["setName"] = { $this['name'] = "++ 1 ++ "; };
&class_bar["setName"] = { $this['name'] = "== $1 =="; };

#
# test it all out
#
$x_foo = lambda(&object, $parent => &class_foo);
$x_bar = lambda(&object, $parent => &class_bar);

$y_foo = lambda(&object, $parent => &class_foo);
$y_bar = lambda(&object, $parent => &class_bar);

[$x_foo printme];
[$y_foo printme];

[$x_bar printme];
[$y_bar printme];

[$x_foo setName: "x_foo!"];
[$x_foo printme];
[$y_foo printme];

[$x_bar setName: "y_bar!"];
[$x_bar printme];
[$y_bar printme];

