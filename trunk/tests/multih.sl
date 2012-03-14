#
# test passing of a multidim data structure as a sleep array
#

import org.hick.blah.SqueezeBox from: data/test.jar;

$sb = [new SqueezeBox];
[$sb doStuff: @(@(1.0, 2.0, 3.0), @(4.0, 5.0, 6.0, 6.5), @(7.0, 8.0, 9.0))];

[$sb doStuff: cast(@(@(1.0, 2.0, 3.0), @(4.0, 5.0, 6.0, 6.5), @(7.0, 8.0, 9.0)), "d", 2, 5)];
[$sb doStuff: cast(@(@(1.0, 2.0, 3.0), @(4.0, 5.0, 6.0, 6.5), @(7.0, 8.0, 9.0)), "d", 5, 2)];

[$sb doStuff: @(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)];

