#
# this recreates a  bug with serializing $null.  Basically a deserialized $null was not seen as
# being the singleton $null associated with the current script environment.  This tended to create
# some problems in serialized/deserialized scripts.  I fixed it though :)
#

$f = {
  local('$handle');
  $handle = [SleepUtils getIOHandle: $null, [System out]];
  println($handle, "test passed!");
  closef($handle);
};

$object = unpack('o', pack('o', $f))[0];
invoke($object);

