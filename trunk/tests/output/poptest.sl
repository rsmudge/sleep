@('a', 'b', 'c', 'd', 'e')
sublist(@a, 0, 0) = @()
sublist(@a, 0, 1) = @('a')
   Popped: a
      @('b', 'c', 'd', 'e')
      @()
sublist(@a, 0, 2) = @('a', 'b')
   Popped: b
      @('a', 'c', 'd', 'e')
      @('a')
   Popped: a
      @('c', 'd', 'e')
      @()
sublist(@a, 0, 3) = @('a', 'b', 'c')
   Popped: c
      @('a', 'b', 'd', 'e')
      @('a', 'b')
   Popped: b
      @('a', 'd', 'e')
      @('a')
   Popped: a
      @('d', 'e')
      @()
sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Popped: d
      @('a', 'b', 'c', 'e')
      @('a', 'b', 'c')
   Popped: c
      @('a', 'b', 'e')
      @('a', 'b')
   Popped: b
      @('a', 'e')
      @('a')
   Popped: a
      @('e')
      @()
sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Popped: e
      @('a', 'b', 'c', 'd')
      @('a', 'b', 'c', 'd')
   Popped: d
      @('a', 'b', 'c')
      @('a', 'b', 'c')
   Popped: c
      @('a', 'b')
      @('a', 'b')
   Popped: b
      @('a')
      @('a')
   Popped: a
      @()
      @()
sublist(@a, 1, 1) = @()
sublist(@a, 1, 2) = @('b')
   Popped: b
      @('a', 'c', 'd', 'e')
      @()
sublist(@a, 1, 3) = @('b', 'c')
   Popped: c
      @('a', 'b', 'd', 'e')
      @('b')
   Popped: b
      @('a', 'd', 'e')
      @()
sublist(@a, 1, 4) = @('b', 'c', 'd')
   Popped: d
      @('a', 'b', 'c', 'e')
      @('b', 'c')
   Popped: c
      @('a', 'b', 'e')
      @('b')
   Popped: b
      @('a', 'e')
      @()
sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Popped: e
      @('a', 'b', 'c', 'd')
      @('b', 'c', 'd')
   Popped: d
      @('a', 'b', 'c')
      @('b', 'c')
   Popped: c
      @('a', 'b')
      @('b')
   Popped: b
      @('a')
      @()
sublist(@a, 2, 2) = @()
sublist(@a, 2, 3) = @('c')
   Popped: c
      @('a', 'b', 'd', 'e')
      @()
sublist(@a, 2, 4) = @('c', 'd')
   Popped: d
      @('a', 'b', 'c', 'e')
      @('c')
   Popped: c
      @('a', 'b', 'e')
      @()
sublist(@a, 2, 5) = @('c', 'd', 'e')
   Popped: e
      @('a', 'b', 'c', 'd')
      @('c', 'd')
   Popped: d
      @('a', 'b', 'c')
      @('c')
   Popped: c
      @('a', 'b')
      @()
sublist(@a, 3, 3) = @()
sublist(@a, 3, 4) = @('d')
   Popped: d
      @('a', 'b', 'c', 'e')
      @()
sublist(@a, 3, 5) = @('d', 'e')
   Popped: e
      @('a', 'b', 'c', 'd')
      @('d')
   Popped: d
      @('a', 'b', 'c')
      @()
sublist(@a, 4, 4) = @()
sublist(@a, 4, 5) = @('e')
   Popped: e
      @('a', 'b', 'c', 'd')
      @()
sublist(@a, 5, 5) = @()
