@('a', 'b', 'c', 'd', 'e')
sublist(@a, 0, 0) = @()
   Push END
      @('END')
      @('END', 'a', 'b', 'c', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'END')
      @('BEGIN', 'END', 'a', 'b', 'c', 'd', 'e')

sublist(@a, 0, 1) = @('a')
   Push END
      @('a', 'END')
      @('a', 'END', 'b', 'c', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'a', 'END')
      @('BEGIN', 'a', 'END', 'b', 'c', 'd', 'e')

sublist(@a, 0, 2) = @('a', 'b')
   Push END
      @('a', 'b', 'END')
      @('a', 'b', 'END', 'c', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'a', 'b', 'END')
      @('BEGIN', 'a', 'b', 'END', 'c', 'd', 'e')

sublist(@a, 0, 3) = @('a', 'b', 'c')
   Push END
      @('a', 'b', 'c', 'END')
      @('a', 'b', 'c', 'END', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'a', 'b', 'c', 'END')
      @('BEGIN', 'a', 'b', 'c', 'END', 'd', 'e')

sublist(@a, 0, 4) = @('a', 'b', 'c', 'd')
   Push END
      @('a', 'b', 'c', 'd', 'END')
      @('a', 'b', 'c', 'd', 'END', 'e')
   Add BEGIN
      @('BEGIN', 'a', 'b', 'c', 'd', 'END')
      @('BEGIN', 'a', 'b', 'c', 'd', 'END', 'e')

sublist(@a, 0, 5) = @('a', 'b', 'c', 'd', 'e')
   Push END
      @('a', 'b', 'c', 'd', 'e', 'END')
      @('a', 'b', 'c', 'd', 'e', 'END')
   Add BEGIN
      @('BEGIN', 'a', 'b', 'c', 'd', 'e', 'END')
      @('BEGIN', 'a', 'b', 'c', 'd', 'e', 'END')

sublist(@a, 1, 1) = @()
   Push END
      @('END')
      @('a', 'END', 'b', 'c', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'END')
      @('a', 'BEGIN', 'END', 'b', 'c', 'd', 'e')

sublist(@a, 1, 2) = @('b')
   Push END
      @('b', 'END')
      @('a', 'b', 'END', 'c', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'b', 'END')
      @('a', 'BEGIN', 'b', 'END', 'c', 'd', 'e')

sublist(@a, 1, 3) = @('b', 'c')
   Push END
      @('b', 'c', 'END')
      @('a', 'b', 'c', 'END', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'b', 'c', 'END')
      @('a', 'BEGIN', 'b', 'c', 'END', 'd', 'e')

sublist(@a, 1, 4) = @('b', 'c', 'd')
   Push END
      @('b', 'c', 'd', 'END')
      @('a', 'b', 'c', 'd', 'END', 'e')
   Add BEGIN
      @('BEGIN', 'b', 'c', 'd', 'END')
      @('a', 'BEGIN', 'b', 'c', 'd', 'END', 'e')

sublist(@a, 1, 5) = @('b', 'c', 'd', 'e')
   Push END
      @('b', 'c', 'd', 'e', 'END')
      @('a', 'b', 'c', 'd', 'e', 'END')
   Add BEGIN
      @('BEGIN', 'b', 'c', 'd', 'e', 'END')
      @('a', 'BEGIN', 'b', 'c', 'd', 'e', 'END')

sublist(@a, 2, 2) = @()
   Push END
      @('END')
      @('a', 'b', 'END', 'c', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'END')
      @('a', 'b', 'BEGIN', 'END', 'c', 'd', 'e')

sublist(@a, 2, 3) = @('c')
   Push END
      @('c', 'END')
      @('a', 'b', 'c', 'END', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'c', 'END')
      @('a', 'b', 'BEGIN', 'c', 'END', 'd', 'e')

sublist(@a, 2, 4) = @('c', 'd')
   Push END
      @('c', 'd', 'END')
      @('a', 'b', 'c', 'd', 'END', 'e')
   Add BEGIN
      @('BEGIN', 'c', 'd', 'END')
      @('a', 'b', 'BEGIN', 'c', 'd', 'END', 'e')

sublist(@a, 2, 5) = @('c', 'd', 'e')
   Push END
      @('c', 'd', 'e', 'END')
      @('a', 'b', 'c', 'd', 'e', 'END')
   Add BEGIN
      @('BEGIN', 'c', 'd', 'e', 'END')
      @('a', 'b', 'BEGIN', 'c', 'd', 'e', 'END')

sublist(@a, 3, 3) = @()
   Push END
      @('END')
      @('a', 'b', 'c', 'END', 'd', 'e')
   Add BEGIN
      @('BEGIN', 'END')
      @('a', 'b', 'c', 'BEGIN', 'END', 'd', 'e')

sublist(@a, 3, 4) = @('d')
   Push END
      @('d', 'END')
      @('a', 'b', 'c', 'd', 'END', 'e')
   Add BEGIN
      @('BEGIN', 'd', 'END')
      @('a', 'b', 'c', 'BEGIN', 'd', 'END', 'e')

sublist(@a, 3, 5) = @('d', 'e')
   Push END
      @('d', 'e', 'END')
      @('a', 'b', 'c', 'd', 'e', 'END')
   Add BEGIN
      @('BEGIN', 'd', 'e', 'END')
      @('a', 'b', 'c', 'BEGIN', 'd', 'e', 'END')

sublist(@a, 4, 4) = @()
   Push END
      @('END')
      @('a', 'b', 'c', 'd', 'END', 'e')
   Add BEGIN
      @('BEGIN', 'END')
      @('a', 'b', 'c', 'd', 'BEGIN', 'END', 'e')

sublist(@a, 4, 5) = @('e')
   Push END
      @('e', 'END')
      @('a', 'b', 'c', 'd', 'e', 'END')
   Add BEGIN
      @('BEGIN', 'e', 'END')
      @('a', 'b', 'c', 'd', 'BEGIN', 'e', 'END')

sublist(@a, 5, 5) = @()
   Push END
      @('END')
      @('a', 'b', 'c', 'd', 'e', 'END')
   Add BEGIN
      @('BEGIN', 'END')
      @('a', 'b', 'c', 'd', 'e', 'BEGIN', 'END')

