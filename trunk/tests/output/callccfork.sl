Trace: &global('$handle $value') at callccfork.sl:18
Trace: function('&func') = &closure[callccfork.sl:9-15]#X at callccfork.sl:20
Begin!
Trace: &println('Begin!') at callccfork.sl:9
Inside of callcc function
Trace: &println('Inside of callcc function') at callccfork.sl:12
Trace: [&closure[callccfork.sl:12-13]#X CALLCC: &closure[callccfork.sl:9-15]#X] = 'pHEAR' at callccfork.sl:10
Trace: &fork(&closure[callccfork.sl:9-15]#X) = sleep.bridges.io.IOObject@###### at callccfork.sl:20
Trace: &wait(sleep.bridges.io.IOObject@######, 5000) = 'pHEAR' at callccfork.sl:21
pHEAR
Trace: &println('pHEAR') at callccfork.sl:22
