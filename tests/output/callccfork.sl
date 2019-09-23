Trace: &global('$handle $value') at callccfork.sl:18
Trace: function('&func') = &closure[callccfork.sl:9-15]#1 at callccfork.sl:20
Begin!
Trace: &fork(&closure[callccfork.sl:9-15]#1) = sleep.bridges.io.IOObject@5cada3d6 at callccfork.sl:20
Trace: &println('Begin!') at callccfork.sl:9
Inside of callcc function
Trace: &println('Inside of callcc function') at callccfork.sl:12
Trace: [&closure[callccfork.sl:12-13]#4 CALLCC: &closure[callccfork.sl:9-15]#3] = 'pHEAR' at callccfork.sl:10
Trace: &wait(sleep.bridges.io.IOObject@5cada3d6, 5000) = 'pHEAR' at callccfork.sl:21
pHEAR
Trace: &println('pHEAR') at callccfork.sl:22
