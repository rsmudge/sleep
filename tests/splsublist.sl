#
#
#

# @subs             0    1    2    3    4    5
#         0    1    2    3    4    5    6    7    8
@list = @("a", "b", "c", "d", "e", "f", "g", "h", "i");
@subs = sublist(@list, 2, 8);
println(@subs);
splice(@subs, @("foo", "bar"), 2, 3);
println(@subs);
println(@list);

