Testing yo
Trace: &println('Testing yo') at include2.sl:3
Blargh
Trace: &println('Blargh') at include2.sl:4
Trace: &lambda(&closure[accum.sl:7-8]#3, $i => 3) = &closure[accum.sl:7-8]#4 at accum.sl:6
Trace: &accum(3) = &closure[accum.sl:7-8]#4 at accum.sl:12
Warning: variable '$a' not declared at accum.sl:12
Trace: &lambda(&closure[accum.sl:7-8]#5, $i => 40) = &closure[accum.sl:7-8]#6 at accum.sl:6
Trace: &accum(40) = &closure[accum.sl:7-8]#6 at accum.sl:13
Warning: variable '$b' not declared at accum.sl:13
Warning: variable '$x' not declared at accum.sl:15
Trace: [&closure[accum.sl:7-8]#6: 2] = 42 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 4 at accum.sl:17
Accumulate: a: 4 b: 42
Trace: &println('Accumulate: a: 4 b: 42') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 44 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 5 at accum.sl:17
Accumulate: a: 5 b: 44
Trace: &println('Accumulate: a: 5 b: 44') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 46 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 6 at accum.sl:17
Accumulate: a: 6 b: 46
Trace: &println('Accumulate: a: 6 b: 46') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 48 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 7 at accum.sl:17
Accumulate: a: 7 b: 48
Trace: &println('Accumulate: a: 7 b: 48') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 50 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 8 at accum.sl:17
Accumulate: a: 8 b: 50
Trace: &println('Accumulate: a: 8 b: 50') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 52 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 9 at accum.sl:17
Accumulate: a: 9 b: 52
Trace: &println('Accumulate: a: 9 b: 52') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 54 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 10 at accum.sl:17
Accumulate: a: 10 b: 54
Trace: &println('Accumulate: a: 10 b: 54') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 56 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 11 at accum.sl:17
Accumulate: a: 11 b: 56
Trace: &println('Accumulate: a: 11 b: 56') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 58 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 12 at accum.sl:17
Accumulate: a: 12 b: 58
Trace: &println('Accumulate: a: 12 b: 58') at accum.sl:17
Trace: [&closure[accum.sl:7-8]#6: 2] = 60 at accum.sl:17
Trace: [&closure[accum.sl:7-8]#4: 1] = 13 at accum.sl:17
Accumulate: a: 13 b: 60
Trace: &println('Accumulate: a: 13 b: 60') at accum.sl:17
Trace: &include('accum.sl') at include2.sl:8
Trace: &foof() at include2.sl:11
