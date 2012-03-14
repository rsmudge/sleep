#
# test of the new isa op
#

debug(7 | 64);

assert "blah" isa ^String;
assert [new LinkedList] isa ^List;
assert 3 isa ^Integer;
assert 3 isa ^Number;
assert 3.0 isa ^Number;
assert 3.0 isa ^Double;
assert 3.0 !isa ^Integer;
assert 5 isa (^Integer);
assert "blah" isa "bleh";
