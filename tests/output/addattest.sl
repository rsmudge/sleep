@('a', 'b', 'c', 'd', 'e')
sublist(@a, 0, 0) = @()
  add at 0:
     @('HELLO', 'a', 'b', 'c', 'd', 'e')
     @('HELLO')
sublist(@a, 0, 1) = @('a')
  add at 0:
     @('HELLO', 'a', 'b', 'c', 'd', 'e')
     @('HELLO', 'a')
sublist(@a, 0, 1) = @('a')
  add at 1:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('a', 'HELLO')
sublist(@a, 0, 2) = @('a', 'b')
  add at 0:
     @('HELLO', 'a', 'b', 'c', 'd', 'e')
     @('HELLO', 'a', 'b')
sublist(@a, 0, 2) = @('a', 'b')
  add at 1:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('a', 'HELLO', 'b')
sublist(@a, 0, 2) = @('a', 'b')
  add at 2:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('a', 'b', 'HELLO')
sublist(@a, 0, 3) = @('a', 'b', 'c')
  add at 0:
     @('HELLO', 'a', 'b', 'c', 'd', 'e')
     @('HELLO', 'a', 'b', 'c')
sublist(@a, 0, 3) = @('a', 'b', 'c')
  add at 1:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('a', 'HELLO', 'b', 'c')
sublist(@a, 0, 3) = @('a', 'b', 'c')
  add at 2:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('a', 'b', 'HELLO', 'c')
sublist(@a, 0, 3) = @('a', 'b', 'c')
  add at 3:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('a', 'b', 'c', 'HELLO')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
  add at 0:
     @('HELLO', 'a', 'b', 'c', 'd', 'e')
     @('HELLO', 'a', 'b', 'c', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
  add at 1:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('a', 'HELLO', 'b', 'c', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
  add at 2:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('a', 'b', 'HELLO', 'c', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
  add at 3:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('a', 'b', 'c', 'HELLO', 'd')
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
  add at 4:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('a', 'b', 'c', 'd', 'HELLO')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
  add at 0:
     @('HELLO', 'a', 'b', 'c', 'd', 'e')
     @('HELLO', 'a', 'b', 'c', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
  add at 1:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
  add at 2:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
  add at 3:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
  add at 4:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
  add at 5:
     @('a', 'b', 'c', 'd', 'e', 'HELLO')
     @('a', 'b', 'c', 'd', 'e', 'HELLO')
sublist(@a, 1, 1) = @()
  add at 0:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('HELLO')
sublist(@a, 1, 2) = @('b')
  add at 0:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('HELLO', 'b')
sublist(@a, 1, 2) = @('b')
  add at 1:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('b', 'HELLO')
sublist(@a, 1, 3) = @('b', 'c')
  add at 0:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('HELLO', 'b', 'c')
sublist(@a, 1, 3) = @('b', 'c')
  add at 1:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('b', 'HELLO', 'c')
sublist(@a, 1, 3) = @('b', 'c')
  add at 2:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('b', 'c', 'HELLO')
sublist(@a, 1, 4) = @('b', 'c', 'd')
  add at 0:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('HELLO', 'b', 'c', 'd')
sublist(@a, 1, 4) = @('b', 'c', 'd')
  add at 1:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('b', 'HELLO', 'c', 'd')
sublist(@a, 1, 4) = @('b', 'c', 'd')
  add at 2:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('b', 'c', 'HELLO', 'd')
sublist(@a, 1, 4) = @('b', 'c', 'd')
  add at 3:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('b', 'c', 'd', 'HELLO')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
  add at 0:
     @('a', 'HELLO', 'b', 'c', 'd', 'e')
     @('HELLO', 'b', 'c', 'd', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
  add at 1:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('b', 'HELLO', 'c', 'd', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
  add at 2:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('b', 'c', 'HELLO', 'd', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
  add at 3:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('b', 'c', 'd', 'HELLO', 'e')
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
  add at 4:
     @('a', 'b', 'c', 'd', 'e', 'HELLO')
     @('b', 'c', 'd', 'e', 'HELLO')
sublist(@a, 2, 2) = @()
  add at 0:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('HELLO')
sublist(@a, 2, 3) = @('c')
  add at 0:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('HELLO', 'c')
sublist(@a, 2, 3) = @('c')
  add at 1:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('c', 'HELLO')
sublist(@a, 2, 4) = @('c', 'd')
  add at 0:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('HELLO', 'c', 'd')
sublist(@a, 2, 4) = @('c', 'd')
  add at 1:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('c', 'HELLO', 'd')
sublist(@a, 2, 4) = @('c', 'd')
  add at 2:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('c', 'd', 'HELLO')
sublist(@a, 2, 5) = @('c', 'd', 'e')
  add at 0:
     @('a', 'b', 'HELLO', 'c', 'd', 'e')
     @('HELLO', 'c', 'd', 'e')
sublist(@a, 2, 5) = @('c', 'd', 'e')
  add at 1:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('c', 'HELLO', 'd', 'e')
sublist(@a, 2, 5) = @('c', 'd', 'e')
  add at 2:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('c', 'd', 'HELLO', 'e')
sublist(@a, 2, 5) = @('c', 'd', 'e')
  add at 3:
     @('a', 'b', 'c', 'd', 'e', 'HELLO')
     @('c', 'd', 'e', 'HELLO')
sublist(@a, 3, 3) = @()
  add at 0:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('HELLO')
sublist(@a, 3, 4) = @('d')
  add at 0:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('HELLO', 'd')
sublist(@a, 3, 4) = @('d')
  add at 1:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('d', 'HELLO')
sublist(@a, 3, 5) = @('d', 'e')
  add at 0:
     @('a', 'b', 'c', 'HELLO', 'd', 'e')
     @('HELLO', 'd', 'e')
sublist(@a, 3, 5) = @('d', 'e')
  add at 1:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('d', 'HELLO', 'e')
sublist(@a, 3, 5) = @('d', 'e')
  add at 2:
     @('a', 'b', 'c', 'd', 'e', 'HELLO')
     @('d', 'e', 'HELLO')
sublist(@a, 4, 4) = @()
  add at 0:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('HELLO')
sublist(@a, 4, 5) = @('e')
  add at 0:
     @('a', 'b', 'c', 'd', 'HELLO', 'e')
     @('HELLO', 'e')
sublist(@a, 4, 5) = @('e')
  add at 1:
     @('a', 'b', 'c', 'd', 'e', 'HELLO')
     @('e', 'HELLO')
sublist(@a, 5, 5) = @()
  add at 0:
     @('a', 'b', 'c', 'd', 'e', 'HELLO')
     @('HELLO')
