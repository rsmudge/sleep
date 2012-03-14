#
# inspired by Python PEP 255... recursive generators for Sleep
#

# do an inorder traversal of a tree closure
sub inorder
{
   local('$x');

   if ($1 !is $null)
   {
      while $x (inorder([$1 left]))
      {
         yield $x;
      }

      yield [$1 label];

      while $x (inorder([$1 right]))
      {
         yield $x;
      } 
   }

   return $null;
}

# do a pre order traversal of a tree closure
sub preorder
{
   local('$x');

   if ($1 !is $null)
   {
      while $x (preorder([$1 left]))
      {
         yield $x;
      }

      while $x (preorder([$1 right]))
      {
         yield $x;
      } 

      yield [$1 label];
   }

   return $null;
}

# do a post order traversal of a tree closure
sub postorder
{
   local('$x');

   if ($1 !is $null)
   {
      yield [$1 label];

      while $x (postorder([$1 left]))
      {
         yield $x;
      }

      while $x (postorder([$1 right]))
      {
         yield $x;
      } 
   }

   return $null;
}


sub tree
{
   this('$label $left $right');

   if ($0 eq "print")
   {
      return "( $+ $label $+ " . iff ($left !is $null, " " . [$left print], " ()") . iff($right !is $null, " " . [$right print], " ()") . ")";
   }

   if ($0 eq "left")
   {
      return $left;
   }

   if ($0 eq "right")
   {
      return $right;
   }

   if ($0 eq "label")
   {
      return $label;
   }
}

sub node
{
   return lambda(&tree, $label => $1, $left => $2, $right => $3);
} 

$root = node("root", node("+", node(3), node(4)), node("*", node("-", node(6), node(2)), node(30)));

println("In order tree traversal");

while $traversal (inorder($root))
{
   println($traversal);
}

println("Pre order tree traversal");

while $traversal (preorder($root))
{
   println($traversal);
}

println("Post order tree traversal");

while $traversal (postorder($root))
{
   println($traversal);
}

