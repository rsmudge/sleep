sublist(@a, 0, 1) = @('a')
   Foreach Remove 0:
     @('b', 'c', 'd', 'e')
     @()
sublist(@a, 0, 2) = @('a', 'b')
   Foreach Remove 0:
     @('b', 'c', 'd', 'e')
     @('b')
sublist(@a, 0, 2) = @('a', 'b')
   Foreach Remove 1:
     @('a', 'c', 'd', 'e')
     @('a')
sublist(@a, 0, 3) = @('a', 'b', 'c')
   Foreach Remove 0:
     @('b', 'c', 'd', 'e')
     @('b', 'c')
sublist(@a, 0, 3) = @('a', 'b', 'c')
   Foreach Remove 1:
     @('a', 'c', 'd', 'e')
     @('a', 'c')
sublist(@a, 0, 3) = @('a', 'b', 'c')
   Foreach Remove 2:
     @('a', 'b', 'd', 'e')
     @('a', 'b')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Foreach Remove 0:
     @('b', 'c', 'd', 'e')
     @('b', 'c', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Foreach Remove 1:
     @('a', 'c', 'd', 'e')
     @('a', 'c', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Foreach Remove 2:
     @('a', 'b', 'd', 'e')
     @('a', 'b', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Foreach Remove 3:
     @('a', 'b', 'c', 'e')
     @('a', 'b', 'c')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Foreach Remove 0:
     @('b', 'c', 'd', 'e')
     @('b', 'c', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Foreach Remove 1:
     @('a', 'c', 'd', 'e')
     @('a', 'c', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Foreach Remove 2:
     @('a', 'b', 'd', 'e')
     @('a', 'b', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Foreach Remove 3:
     @('a', 'b', 'c', 'e')
     @('a', 'b', 'c', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Foreach Remove 4:
     @('a', 'b', 'c', 'd')
     @('a', 'b', 'c', 'd')
sublist(@a, 1, 2) = @('b')
   Foreach Remove 0:
     @('a', 'c', 'd', 'e')
     @()
sublist(@a, 1, 3) = @('b', 'c')
   Foreach Remove 0:
     @('a', 'c', 'd', 'e')
     @('c')
sublist(@a, 1, 3) = @('b', 'c')
   Foreach Remove 1:
     @('a', 'b', 'd', 'e')
     @('b')
sublist(@a, 1, 4) = @('b', 'c', 'd')
   Foreach Remove 0:
     @('a', 'c', 'd', 'e')
     @('c', 'd')
sublist(@a, 1, 4) = @('b', 'c', 'd')
   Foreach Remove 1:
     @('a', 'b', 'd', 'e')
     @('b', 'd')
sublist(@a, 1, 4) = @('b', 'c', 'd')
   Foreach Remove 2:
     @('a', 'b', 'c', 'e')
     @('b', 'c')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Foreach Remove 0:
     @('a', 'c', 'd', 'e')
     @('c', 'd', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Foreach Remove 1:
     @('a', 'b', 'd', 'e')
     @('b', 'd', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Foreach Remove 2:
     @('a', 'b', 'c', 'e')
     @('b', 'c', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Foreach Remove 3:
     @('a', 'b', 'c', 'd')
     @('b', 'c', 'd')
sublist(@a, 2, 3) = @('c')
   Foreach Remove 0:
     @('a', 'b', 'd', 'e')
     @()
sublist(@a, 2, 4) = @('c', 'd')
   Foreach Remove 0:
     @('a', 'b', 'd', 'e')
     @('d')
sublist(@a, 2, 4) = @('c', 'd')
   Foreach Remove 1:
     @('a', 'b', 'c', 'e')
     @('c')
sublist(@a, 2, 5) = @('c', 'd', 'e')
   Foreach Remove 0:
     @('a', 'b', 'd', 'e')
     @('d', 'e')
sublist(@a, 2, 5) = @('c', 'd', 'e')
   Foreach Remove 1:
     @('a', 'b', 'c', 'e')
     @('c', 'e')
sublist(@a, 2, 5) = @('c', 'd', 'e')
   Foreach Remove 2:
     @('a', 'b', 'c', 'd')
     @('c', 'd')
sublist(@a, 3, 4) = @('d')
   Foreach Remove 0:
     @('a', 'b', 'c', 'e')
     @()
sublist(@a, 3, 5) = @('d', 'e')
   Foreach Remove 0:
     @('a', 'b', 'c', 'e')
     @('e')
sublist(@a, 3, 5) = @('d', 'e')
   Foreach Remove 1:
     @('a', 'b', 'c', 'd')
     @('d')
sublist(@a, 4, 5) = @('e')
   Foreach Remove 0:
     @('a', 'b', 'c', 'd')
     @()
