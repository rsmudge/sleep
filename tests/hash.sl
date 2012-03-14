#
# Test some stuff related to hash data
#

%blah['secret'] = 31337;
%blah[42]       = "life the universe, and everything";

printf("What is the 'secret': " . %blah['secret']);
printf("What is the significance of 42? " . %blah[42]);
