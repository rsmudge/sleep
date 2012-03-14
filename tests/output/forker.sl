Trace: &fork(&closure[forker.sl:9-10]#2) = sleep.bridges.io.IOObject@d5eb7 at forker.sl:8
Trace: &sleep(1000) at forker.sl:9
Trace: &check('within fork') at forker.sl:10
Trace: &wait(sleep.bridges.io.IOObject@d5eb7, 5000) at forker.sl:8
Trace: &check('outside of fork') at forker.sl:13
