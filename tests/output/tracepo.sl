Trace: &local('$enumeration') at tracepo.sl:25
Trace: function('&object') = &closure[tracepo.sl:7-15]#1 at tracepo.sl:26
Trace: &lambda(&closure[tracepo.sl:7-15]#1) = &closure[tracepo.sl:7-15]#3 at tracepo.sl:26
Trace: &foo() = &closure[tracepo.sl:7-15]#3 at tracepo.sl:45
Trace: [&closure[tracepo.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#4, @(), $this => &closure[tracepo.sl:7-15]#3) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c', 'd', 'e')) = 5 at tracepo.sl:30
Trace: &pop(@('a', 'b', 'c', 'd', 'e')) = 'e' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#5] = 'e' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#5, @(), $this => &closure[tracepo.sl:7-15]#3) = 'e' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 nextElement] = 'e' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#4, @(), $this => &closure[tracepo.sl:7-15]#3) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c', 'd')) = 4 at tracepo.sl:30
Trace: &pop(@('a', 'b', 'c', 'd')) = 'd' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#5] = 'd' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#5, @(), $this => &closure[tracepo.sl:7-15]#3) = 'd' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 nextElement] = 'd' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#4, @(), $this => &closure[tracepo.sl:7-15]#3) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c')) = 3 at tracepo.sl:30
Trace: &pop(@('a', 'b', 'c')) = 'c' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#5] = 'c' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#5, @(), $this => &closure[tracepo.sl:7-15]#3) = 'c' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 nextElement] = 'c' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#4, @(), $this => &closure[tracepo.sl:7-15]#3) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b')) = 2 at tracepo.sl:30
Trace: &pop(@('a', 'b')) = 'b' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#5] = 'b' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#5, @(), $this => &closure[tracepo.sl:7-15]#3) = 'b' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 nextElement] = 'b' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#4, @(), $this => &closure[tracepo.sl:7-15]#3) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a')) = 1 at tracepo.sl:30
Trace: &pop(@('a')) = 'a' at tracepo.sl:32
Trace: [&closure[tracepo.sl:30-36]#5] = 'a' at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#5, @(), $this => &closure[tracepo.sl:7-15]#3) = 'a' at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 nextElement] = 'a' at <Java>:-1
Trace: [&closure[tracepo.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:28]#4, @(), $this => &closure[tracepo.sl:7-15]#3) = 1 at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@()) = 0 at tracepo.sl:30
Trace: [new java.util.NoSuchElementException: 'overextending my bounds dude :('] = java.util.NoSuchElementException: overextending my bounds dude :( at tracepo.sl:36
Trace: [&closure[tracepo.sl:30-36]#5] - FAILED! at <internal>:-1
Trace: &invoke(&closure[tracepo.sl:30-36]#5, @(), $this => &closure[tracepo.sl:7-15]#3) - FAILED! at tracepo.sl:11
Trace: [&closure[tracepo.sl:7-15]#3 nextElement] - FAILED! at <Java>:-1
Trace: [java.util.Collections list: &closure[tracepo.sl:7-15]#3] - FAILED! at tracepo.sl:45
Warning: checkError(): java.util.NoSuchElementException: overextending my bounds dude :( at tracepo.sl:45

Trace: &println($null) at tracepo.sl:45
Trying again... what will java do?
Trace: &println('Trying again... what will java do?') at tracepo.sl:55
Trace: [&closure[tracepo.sl:57]#6 hasMoreElements] - FAILED! at <Java>:-1
Trace: [java.util.Collections list: &closure[tracepo.sl:57]#6] - FAILED! at tracepo.sl:56
Warning: checkError(): java.lang.RuntimeException: haha... testing bish!@#$ at tracepo.sl:56

Trace: &println($null) at tracepo.sl:56
