@('a', 'b', 'c', 'd', 'e')
sublist(@a, 0, 1) = @('a')
   Remove 0:
     @('b', 'c', 'd', 'e')
     @()
sublist(@a, 0, 2) = @('a', 'b')
   Remove 0:
     @('b', 'c', 'd', 'e')
     @('b')
sublist(@a, 0, 2) = @('a', 'b')
   Remove 1:
     @('a', 'c', 'd', 'e')
     @('a')
sublist(@a, 0, 3) = @('a', 'b', 'c')
   Remove 0:
     @('b', 'c', 'd', 'e')
     @('b', 'c')
sublist(@a, 0, 3) = @('a', 'b', 'c')
   Remove 1:
     @('a', 'c', 'd', 'e')
     @('a', 'c')
sublist(@a, 0, 3) = @('a', 'b', 'c')
   Remove 2:
     @('a', 'b', 'd', 'e')
     @('a', 'b')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Remove 0:
     @('b', 'c', 'd', 'e')
     @('b', 'c', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Remove 1:
     @('a', 'c', 'd', 'e')
     @('a', 'c', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Remove 2:
     @('a', 'b', 'd', 'e')
     @('a', 'b', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Remove 3:
     @('a', 'b', 'c', 'e')
     @('a', 'b', 'c')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Remove 0:
     @('b', 'c', 'd', 'e')
     @('b', 'c', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Remove 1:
     @('a', 'c', 'd', 'e')
     @('a', 'c', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Remove 2:
     @('a', 'b', 'd', 'e')
     @('a', 'b', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Remove 3:
     @('a', 'b', 'c', 'e')
     @('a', 'b', 'c', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Remove 4:
     @('a', 'b', 'c', 'd')
     @('a', 'b', 'c', 'd')
sublist(@a, 1, 2) = @('b')
   Remove 0:
     @('a', 'c', 'd', 'e')
     @()
sublist(@a, 1, 3) = @('b', 'c')
   Remove 0:
     @('a', 'c', 'd', 'e')
     @('c')
sublist(@a, 1, 3) = @('b', 'c')
   Remove 1:
     @('a', 'b', 'd', 'e')
     @('b')
sublist(@a, 1, 4) = @('b', 'c', 'd')
   Remove 0:
     @('a', 'c', 'd', 'e')
     @('c', 'd')
sublist(@a, 1, 4) = @('b', 'c', 'd')
   Remove 1:
     @('a', 'b', 'd', 'e')
     @('b', 'd')
sublist(@a, 1, 4) = @('b', 'c', 'd')
   Remove 2:
     @('a', 'b', 'c', 'e')
     @('b', 'c')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Remove 0:
     @('a', 'c', 'd', 'e')
     @('c', 'd', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Remove 1:
     @('a', 'b', 'd', 'e')
     @('b', 'd', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Remove 2:
     @('a', 'b', 'c', 'e')
     @('b', 'c', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Remove 3:
     @('a', 'b', 'c', 'd')
     @('b', 'c', 'd')
sublist(@a, 2, 3) = @('c')
   Remove 0:
     @('a', 'b', 'd', 'e')
     @()
sublist(@a, 2, 4) = @('c', 'd')
   Remove 0:
     @('a', 'b', 'd', 'e')
     @('d')
sublist(@a, 2, 4) = @('c', 'd')
   Remove 1:
     @('a', 'b', 'c', 'e')
     @('c')
sublist(@a, 2, 5) = @('c', 'd', 'e')
   Remove 0:
     @('a', 'b', 'd', 'e')
     @('d', 'e')
sublist(@a, 2, 5) = @('c', 'd', 'e')
   Remove 1:
     @('a', 'b', 'c', 'e')
     @('c', 'e')
sublist(@a, 2, 5) = @('c', 'd', 'e')
   Remove 2:
     @('a', 'b', 'c', 'd')
     @('c', 'd')
sublist(@a, 3, 4) = @('d')
   Remove 0:
     @('a', 'b', 'c', 'e')
     @()
sublist(@a, 3, 5) = @('d', 'e')
   Remove 0:
     @('a', 'b', 'c', 'e')
     @('e')
sublist(@a, 3, 5) = @('d', 'e')
   Remove 1:
     @('a', 'b', 'c', 'd')
     @('d')
sublist(@a, 4, 5) = @('e')
   Remove 0:
     @('a', 'b', 'c', 'd')
     @()
