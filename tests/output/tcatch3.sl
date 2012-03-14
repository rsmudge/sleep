Trace: &local('$x') at tcatch3.sl:5
Trace: [java.lang.Integer parseInt: 4] = 4 at tcatch3.sl:6
Trace: &local('$x') at tcatch3.sl:5
Trace: [java.lang.Integer parseInt: 3] = 3 at tcatch3.sl:6
Trace: &local('$x') at tcatch3.sl:5
Trace: [java.lang.Integer parseInt: 2] = 2 at tcatch3.sl:6
Trace: &local('$x') at tcatch3.sl:5
Trace: [java.lang.Integer parseInt: 1] = 1 at tcatch3.sl:6
Trace: &local('$x') at tcatch3.sl:5
Trace: [java.lang.Integer parseInt: 0] = 0 at tcatch3.sl:6
Trace: &local('$x') at tcatch3.sl:5
Trace: [java.lang.Integer parseInt: 'shlfjkalsf'] - FAILED! at tcatch3.sl:6
Trace: &recurse('shlfjkalsf') - FAILED! at tcatch3.sl:9
Trace: &recurse(0) - FAILED! at tcatch3.sl:11
Trace: &recurse(1) - FAILED! at tcatch3.sl:11
Trace: &recurse(2) - FAILED! at tcatch3.sl:11
Trace: &recurse(3) - FAILED! at tcatch3.sl:11
Trace: &recurse(4) - FAILED! at tcatch3.sl:16
didn't work: java.lang.NumberFormatException: For input string: "shlfjkalsf"
Trace: &println('didn't work: java.lang.NumberFormatException: For input string: "shlfjkalsf"') at tcatch3.sl:20
Trace: &getStackTrace() = @(   tcatch3.sl:16 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:9 &recurse(),    tcatch3.sl:6 public static int java.lang.Integer.parseInt(java.lang.String) throws java.lang.NumberFormatException) at tcatch3.sl:21
   tcatch3.sl:16 &recurse()
   tcatch3.sl:11 &recurse()
   tcatch3.sl:11 &recurse()
   tcatch3.sl:11 &recurse()
   tcatch3.sl:11 &recurse()
   tcatch3.sl:9 &recurse()
   tcatch3.sl:6 public static int java.lang.Integer.parseInt(java.lang.String) throws java.lang.NumberFormatException
Trace: &printAll(@(   tcatch3.sl:16 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:11 &recurse(),    tcatch3.sl:9 &recurse(),    tcatch3.sl:6 public static int java.lang.Integer.parseInt(java.lang.String) throws java.lang.NumberFormatException)) at tcatch3.sl:21
