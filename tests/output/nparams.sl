Trace: &local('$test') at nparams.sl:9
test1: success!
Trace: &println('test1: success!') at nparams.sl:10
Trace: &test1($test => 'success!') at nparams.sl:13
Trace: &local('$test') at nparams.sl:9
test1: 
Trace: &println('test1: ') at nparams.sl:10
Trace: &test1() at nparams.sl:14
test2: yes, another one
Trace: &println('test2: yes, another one') at nparams.sl:18
Trace: &test2($test => 'yes, another one') at nparams.sl:21
Warning: variable '$test' not declared at nparams.sl:18
test2: 
Trace: &println('test2: ') at nparams.sl:18
Trace: &test2() at nparams.sl:22
Trace: &this('$test') at nparams.sl:26
test3: eh?!?
Trace: &println('test3: eh?!?') at nparams.sl:27
Trace: &test3($test => 'eh?!?') at nparams.sl:31
Trace: &this('$test') at nparams.sl:26
test3: 
Trace: &println('test3: ') at nparams.sl:27
Trace: &test3() at nparams.sl:32
Trace: &this('$test') at nparams.sl:26
test3: :)
Trace: &println('test3: :)') at nparams.sl:27
Trace: &test3() at nparams.sl:33
Trace: &local('$count $var') at nparams.sl:37
0   = a
Trace: &println('0   = a') at nparams.sl:41
1   = b
Trace: &println('1   = b') at nparams.sl:41
2   = c
Trace: &println('2   = c') at nparams.sl:41
3   = d
Trace: &println('3   = d') at nparams.sl:41
a: apple and b: boy and c: cat
Trace: &println('a: apple and b: boy and c: cat') at nparams.sl:44
Trace: &test4('a', 'b', $a => 'apple', 'c', $b => 'boy', 'd', $c => 'cat') at nparams.sl:47
Test 5 has been called, executing action:
Trace: &println('Test 5 has been called, executing action:') at nparams.sl:51
The passed in closure has been called
Trace: &println('The passed in closure has been called') at nparams.sl:55
Trace: [&closure[nparams.sl:55]#6] at nparams.sl:52
Trace: &test5($action => &closure[nparams.sl:55]#6) at nparams.sl:55
Trace: &test5(action => &closure[nparams.sl:56]#7) - FAILED! at nparams.sl:56
Warning: unreachable named parameter: action at nparams.sl:56
