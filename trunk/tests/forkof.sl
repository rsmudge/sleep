#
# 
#

wait(fork({
  println([Thread currentThread]);
}), 5000);
