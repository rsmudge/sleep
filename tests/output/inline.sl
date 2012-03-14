bar
foo
foo rules!
bleh 0
blah 1
Returned pHEAR
blah 3
bleh 2
@_ = @('aa', 'bbb', 'cccc')
Arg: aa
$foo: uNF
$this: &closure[inline.sl:57-60]#3
$key: some value
pHEAR
bar
Trace: &println('bar') at inline.sl:15
foo
Trace: &println('foo') at inline.sl:9
Trace: <inline> &foo() = 'foo rules!' at inline.sl:16
Trace: &bar() = 'foo rules!' at inline.sl:70
Trace: &not(15) = -16 at inline.sl:71
Trace: &debug() = 15 at inline.sl:71
33
   inline.sl:88 <inline> &except()
   inline.sl:79 <origin of exception>
bleh 0
blah 1
Result: pHEAR
blah 3
bleh 2
Result: 
