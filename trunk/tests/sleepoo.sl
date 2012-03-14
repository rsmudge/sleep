#
# this is just a test of using the new closure indexing capability
# to see what kind of fun it brings to the sleep language.
#

# special thanks to Andreas for starting the train of thought that led
# to this.

# this concept can surely be expanded, I was just looking to mimic the
# javascript "prototype" function ability.  

# a generic object... when a message is passed, the objects closure scope is referred to.
# if the message indexes a function, the function is called.
# otherwise a value is returned.  no big deal.  eh. (:
sub object
{
   if ($0 !is $null)
   {
      if (-isfunction $this[$0])
      {
         return invoke($this[$0], @_);
      }
      else
      {
         return $this[$0];
      }
   }  
}

#
# a constructor for some sort of class... keeps track of all instances of
# the "class".  allows slots for each instance to be populated via the
# set message.  new instances are created with the new message.
#
# one weakness with this implementation is that all instances must be created
# prior to populating the slots.  this could be easily fixed but I am just
# creating a test case here, not a full object implementation for sleep.
#
sub construct
{
  this('@mybabies');

  # [&construct set: "key", value]
  if ($0 eq "set") 
  {
     return map(lambda({ 
        if (-isfunction $y)
        {
           $1[$x] = lambda($y, $object => $1); 
        }
        else
        {
           $1[$x] = $y;
        }
     }, $x => $1, $y => $2), @mybabies);
  }
  # [&construct new]
  else if ($0 eq "new")
  {
     return push(@mybabies, lambda(&object, $class => $this));
  }
}

#
# test all of this out
# 

$class_foo = lambda(&construct);
$class_bar = lambda(&construct);  

$x_foo = [$class_foo new];
$x_bar = [$class_bar new];

$y_foo = [$class_foo new];
$y_bar = [$class_bar new];

#
# add a printme method to all instantiated class_foo and class_bar "objects"
#
[$class_foo set: "printme", { println("<foo> my name is: " . [$object name] . "; arg is $1"); }];
[$class_bar set: "printme", { println("<bar> my name is: " . [$object name] . "; arg is $1"); }];

#
# add a name field to all instnatiated class_foo and class_bar objects
#
[$class_foo set: "name", "default foo"];
[$class_bar set: "name", "default bar"];

#
# x will not use the defaults...
#
$x_foo["name"] = "not default x - foo";
$x_bar["name"] = "not default x - bar";

#
# test it all out
#
[$x_foo printme: "x_foo argument"];
[$y_foo printme: "y_foo argument"];

[$x_bar printme: "x_bar argument"];
[$y_bar printme: "y_bar argument"];

