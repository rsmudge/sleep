Trace: &local('$enumeration') at tracepo.sl:25
Trace: function('&object') = &closure[tracepo.sl:7-15]#X at tracepo.sl:26
Trace: &lambda(&closure[tracepo.sl:7-15]#X) = &closure[tracepo.sl:7-15]#X at tracepo.sl:26
Trace: &foo() = &closure[tracepo.sl:7-15]#X at tracepo.sl:45
Trace: [&closure[tracepo.sl:28]#X] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c', 'd', 'e')) = 5 at tracepo.sl:30
Trace: &pop(@('a', 'b', 'c', 'd', 'e')) = 'e' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#X] = 'e' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 'e' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X nextElement] = 'e' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#X] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c', 'd')) = 4 at tracepo.sl:30
Trace: &pop(@('a', 'b', 'c', 'd')) = 'd' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#X] = 'd' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 'd' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X nextElement] = 'd' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#X] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c')) = 3 at tracepo.sl:30
Trace: &pop(@('a', 'b', 'c')) = 'c' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#X] = 'c' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 'c' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X nextElement] = 'c' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#X] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b')) = 2 at tracepo.sl:30
Trace: &pop(@('a', 'b')) = 'b' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#X] = 'b' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 'b' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X nextElement] = 'b' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#X] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a')) = 1 at tracepo.sl:30
Trace: &pop(@('a')) = 'a' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#X] = 'a' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 'a' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X nextElement] = 'a' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#X] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#X, @(), $this => &closure[tracepo.sl:7-15]#X) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X hasMoreElements] = 1 at <Java>:-1
Trace: &size(@()) = 0 at tracepo.sl:30
Trace: [new java.util.NoSuchElementException: 'overextending my bounds dude :('] = java.util.NoSuchElementException: overextending my bounds dude :( at tracepo.sl:36
Trace: [&closure[tracepo.sl:30-36]#X] - FAILED! at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#X, @(), $this => &closure[tracepo.sl:7-15]#X) - FAILED! at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#X nextElement] - FAILED! at <Java>:-1
Trace: [java.util.Collections list: &closure[tracepo.sl:7-15]#X] - FAILED! at tracepo.sl:45
Warning: checkError(): java.util.NoSuchElementException: overextending my bounds dude :( at tracepo.sl:45

Trace: &println($null) at tracepo.sl:45
Trying again... what will java do?
Trace: &println('Trying again... what will java do?') at tracepo.sl:55
Trace: [&closure[tracepo.sl:57]#X hasMoreElements] - FAILED! at <Java>:-1
Trace: [java.util.Collections list: &closure[tracepo.sl:57]#X] - FAILED! at tracepo.sl:56
Warning: checkError(): java.lang.RuntimeException: haha... testing bish!@#$ at tracepo.sl:56

Trace: &println($null) at tracepo.sl:56
