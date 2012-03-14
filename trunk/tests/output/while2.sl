The repeat function has been called
Testing new while syntax: 0
The repeat function has been called
Testing new while syntax: 1
The repeat function has been called
Testing new while syntax: 2
The repeat function has been called
Testing new while syntax: 3
The repeat function has been called
Testing new while syntax: 4
The repeat function has been called
Testing new while syntax: 5
The repeat function has been called
Testing new while syntax: 6
The repeat function has been called
Testing new while syntax: 7
The repeat function has been called
Testing new while syntax: 8
The repeat function has been called
Testing new while syntax: 9
The repeat function has been called
Read: #
Read: # test the extended syntax for while loops...
Read: #
Read: 
Read: sub callto
Read: {
Read:    this('$x');
Read:    $x = 0;
Read:    while ($x < $1)
Read:    {
Read:       yield $x;
Read:       $x++;
Read:    }
Read: 
Read:    return $null;
Read: }
Read: 
Read: sub repeat
Read: {
Read:    println("The repeat function has been called");
Read:    return 10;
Read: }
Read: 
Read: while $check (callto(repeat()))
Read: {
Read:    println("Testing new while syntax: $check");
Read: }
Read: 
Read: # test 2.
Read: 
Read: $handle = openf("while2.sl");
Read: 
Read: while $value (readln($handle))
Read: {
Read:    println("Read: $value");
Read: }
