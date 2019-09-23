this is a test
Trace: [java.io.PrintStream@618c5880 println: 'this is a test'] at trace.sl:6
Trace: [java.lang.Math pow: 3, 4] = 81.0 at trace.sl:7
81.0
Trace: [java.io.PrintStream@618c5880 println: 81.0] at trace.sl:7
Trace: [java.lang.Math pow: 3, 5] = 243.0 at trace.sl:8
243.0
Trace: &println(243.0) at trace.sl:8
testing again...
Trace: [java.io.PrintStream@618c5880 println: 'testing again...'] at trace.sl:10
Trace: [sleep.runtime.SleepUtils getListFromArray: @('a', 'b', 'c')] = [a, b, c] at trace.sl:12
Trace: [new java.util.LinkedList: [a, b, c]] = [a, b, c] at trace.sl:12
Warning: variable '$list' not declared at trace.sl:12
[a, b, c]
Trace: &println([a, b, c]) at trace.sl:14
