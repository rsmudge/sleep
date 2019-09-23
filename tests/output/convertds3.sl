Primitive arrays:
Object a: class java.util.LinkedList
Object a: class java.util.LinkedList
Object a: class java.util.LinkedList
Explicit conversions:
Object a: class [Z
Object a: class [F
Should be objects:
Object[] a: class [Ljava.lang.Object;
a[0] - 1 - class java.lang.Long
a[1] - 2 - class java.lang.Integer
a[2] - 3 - class java.lang.Integer
a[3] - str - class java.lang.String
Warning: 4 at 1 is not compatible with java.util.HashSet at convertds3.sl:17
This stuff is strings, why?
Object[] a: class [Ljava.lang.String;
a[0] - x - class java.lang.String
a[1] - 1 - class java.lang.String
a[2] - 2 - class java.lang.String
a[3] - 3 - class java.lang.String
a[4] - str - class java.lang.String
Car tests:
int[] a
Object a
Object a
Object a
Object a
Object a
Object a
Object a
Mar tests:
int[] a
Collection a
Collection a
Warning: there is no method that matches mar([Z@690edaf3) in sleep.ArrayTest1 at convertds3.sl:36
Warning: there is no method that matches mar([F@4e48bd67) in sleep.ArrayTest1 at convertds3.sl:37
Warning: there is no method that matches mar([Ljava.lang.Object;@98add58) in sleep.ArrayTest1 at convertds3.sl:38
int[] a
Collection a
Collection a
Tar test:
List a: class java.util.LinkedList
