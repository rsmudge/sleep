Trace: &local('$enumeration') at proxy.sl:25
Trace: function('&object') = &closure[proxy.sl:7-15]#1 at proxy.sl:26
Trace: &lambda(&closure[proxy.sl:7-15]#1) = &closure[proxy.sl:7-15]#3 at proxy.sl:26
Trace: &foo() = &closure[proxy.sl:7-15]#3 at proxy.sl:45
Trace: [&closure[proxy.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[proxy.sl:28]#4, @(), $this => &closure[proxy.sl:7-15]#3) = 1 at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c', 'd', 'e')) = 5 at proxy.sl:30
Trace: &pop(@('a', 'b', 'c', 'd', 'e')) = 'e' at proxy.sl:32
Trace: [&closure[proxy.sl:30-36]#5] = 'e' at <internal>:-1
Trace: &invoke(&closure[proxy.sl:30-36]#5, @(), $this => &closure[proxy.sl:7-15]#3) = 'e' at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 nextElement] = 'e' at <Java>:-1
Trace: [&closure[proxy.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[proxy.sl:28]#4, @(), $this => &closure[proxy.sl:7-15]#3) = 1 at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c', 'd')) = 4 at proxy.sl:30
Trace: &pop(@('a', 'b', 'c', 'd')) = 'd' at proxy.sl:32
Trace: [&closure[proxy.sl:30-36]#5] = 'd' at <internal>:-1
Trace: &invoke(&closure[proxy.sl:30-36]#5, @(), $this => &closure[proxy.sl:7-15]#3) = 'd' at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 nextElement] = 'd' at <Java>:-1
Trace: [&closure[proxy.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[proxy.sl:28]#4, @(), $this => &closure[proxy.sl:7-15]#3) = 1 at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b', 'c')) = 3 at proxy.sl:30
Trace: &pop(@('a', 'b', 'c')) = 'c' at proxy.sl:32
Trace: [&closure[proxy.sl:30-36]#5] = 'c' at <internal>:-1
Trace: &invoke(&closure[proxy.sl:30-36]#5, @(), $this => &closure[proxy.sl:7-15]#3) = 'c' at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 nextElement] = 'c' at <Java>:-1
Trace: [&closure[proxy.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[proxy.sl:28]#4, @(), $this => &closure[proxy.sl:7-15]#3) = 1 at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a', 'b')) = 2 at proxy.sl:30
Trace: &pop(@('a', 'b')) = 'b' at proxy.sl:32
Trace: [&closure[proxy.sl:30-36]#5] = 'b' at <internal>:-1
Trace: &invoke(&closure[proxy.sl:30-36]#5, @(), $this => &closure[proxy.sl:7-15]#3) = 'b' at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 nextElement] = 'b' at <Java>:-1
Trace: [&closure[proxy.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[proxy.sl:28]#4, @(), $this => &closure[proxy.sl:7-15]#3) = 1 at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@('a')) = 1 at proxy.sl:30
Trace: &pop(@('a')) = 'a' at proxy.sl:32
Trace: [&closure[proxy.sl:30-36]#5] = 'a' at <internal>:-1
Trace: &invoke(&closure[proxy.sl:30-36]#5, @(), $this => &closure[proxy.sl:7-15]#3) = 'a' at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 nextElement] = 'a' at <Java>:-1
Trace: [&closure[proxy.sl:28]#4] = 1 at <internal>:-1
Trace: &invoke(&closure[proxy.sl:28]#4, @(), $this => &closure[proxy.sl:7-15]#3) = 1 at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 hasMoreElements] = 1 at <Java>:-1
Trace: &size(@()) = 0 at proxy.sl:30
Trace: [new java.util.NoSuchElementException: 'overextending my bounds dude :('] = java.util.NoSuchElementException: overextending my bounds dude :( at proxy.sl:36
Trace: [&closure[proxy.sl:30-36]#5] - FAILED! at <internal>:-1
Trace: &invoke(&closure[proxy.sl:30-36]#5, @(), $this => &closure[proxy.sl:7-15]#3) - FAILED! at proxy.sl:11
Trace: [&closure[proxy.sl:7-15]#3 nextElement] - FAILED! at <Java>:-1
Trace: [java.util.Collections list: &closure[proxy.sl:7-15]#3] - FAILED! at proxy.sl:45
Trace: [java.util.NoSuchElementException: overextending my bounds dude :( getClass] = class java.util.NoSuchElementException at proxy.sl:49
Trace: [java.util.NoSuchElementException: overextending my bounds dude :( getMessage] = 'overextending my bounds dude :(' at proxy.sl:49
Error: overextending my bounds dude :( from: class java.util.NoSuchElementException
Trace: &println('Error: overextending my bounds dude :( from: class java.util.NoSuchElementException') at proxy.sl:49
Trace: &getStackTrace() = @(   proxy.sl:45 public static java.util.ArrayList java.util.Collections.list(java.util.Enumeration),    <Java>:-1 &closure[proxy.sl:7-15]#3 as public abstract java.lang.Object java.util.Enumeration.nextElement(),    proxy.sl:11 &invoke(),    <internal>:-1 &closure[proxy.sl:30-36]#5,    proxy.sl:36 <origin of exception>) at proxy.sl:50
   proxy.sl:45 public static java.util.ArrayList java.util.Collections.list(java.util.Enumeration)
   <Java>:-1 &closure[proxy.sl:7-15]#3 as public abstract java.lang.Object java.util.Enumeration.nextElement()
   proxy.sl:11 &invoke()
   <internal>:-1 &closure[proxy.sl:30-36]#5
   proxy.sl:36 <origin of exception>
Trace: &printAll(@(   proxy.sl:45 public static java.util.ArrayList java.util.Collections.list(java.util.Enumeration),    <Java>:-1 &closure[proxy.sl:7-15]#3 as public abstract java.lang.Object java.util.Enumeration.nextElement(),    proxy.sl:11 &invoke(),    <internal>:-1 &closure[proxy.sl:30-36]#5,    proxy.sl:36 <origin of exception>)) at proxy.sl:50
Trying again... what will java do?
Trace: &println('Trying again... what will java do?') at proxy.sl:55
Trace: [&closure[proxy.sl:57]#6 hasMoreElements] - FAILED! at <Java>:-1
Trace: [java.util.Collections list: &closure[proxy.sl:57]#6] - FAILED! at proxy.sl:56
Trace: [java.lang.RuntimeException: haha... testing bish!@#$ getClass] = class java.lang.RuntimeException at proxy.sl:62
Trace: [java.lang.RuntimeException: haha... testing bish!@#$ getMessage] = 'haha... testing bish!@#$' at proxy.sl:62
Error: haha... testing bish!@#$ from: class java.lang.RuntimeException
Trace: &println('Error: haha... testing bish!@#$ from: class java.lang.RuntimeException') at proxy.sl:62
Trace: &getStackTrace() = @(   proxy.sl:56 public static java.util.ArrayList java.util.Collections.list(java.util.Enumeration),    <Java>:-1 &closure[proxy.sl:57]#6 as public abstract boolean java.util.Enumeration.hasMoreElements(),    proxy.sl:57 <origin of exception>) at proxy.sl:63
   proxy.sl:56 public static java.util.ArrayList java.util.Collections.list(java.util.Enumeration)
   <Java>:-1 &closure[proxy.sl:57]#6 as public abstract boolean java.util.Enumeration.hasMoreElements()
   proxy.sl:57 <origin of exception>
Trace: &printAll(@(   proxy.sl:56 public static java.util.ArrayList java.util.Collections.list(java.util.Enumeration),    <Java>:-1 &closure[proxy.sl:57]#6 as public abstract boolean java.util.Enumeration.hasMoreElements(),    proxy.sl:57 <origin of exception>)) at proxy.sl:63
