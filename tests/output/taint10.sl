before expr: a
Warning: tainted value: 'after  expr: a' from: 'a' at taint10.sl:16
after  expr: a
Warning: tainted value: '... a :)' from: 'a' at taint10.sl:20
... a :)
Warning: tainted value: '... b :)' from: 'b' at taint10.sl:20
... b :)
Warning: tainted value: '... c :)' from: 'c' at taint10.sl:20
... c :)
Warning: tainted value: '... d :)' from: 'd' at taint10.sl:20
... d :)
Warning: tainted value: '... e :)' from: 'e' at taint10.sl:20
... e :)
Before: apple
Warning: tainted value: 'After: cat' from: 'cat' at taint10.sl:34
After: cat
>>> a - should be ok
Warning: tainted value: '... apple :)' from: 'apple' at taint10.sl:39
... apple :)
>>> c - should be ok
Warning: tainted value: '... cat :)' from: 'cat' at taint10.sl:39
... cat :)
>>> b - should be ok
Warning: tainted value: '... barracuda :)' from: 'barracuda' at taint10.sl:39
... barracuda :)
