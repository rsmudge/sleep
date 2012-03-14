#
# Another sorting test...   
#

@data = array("Bleh", "Apple", "Juice", "Rocks", "For", "KiDDiEZ", "bYoTCH", "SlOTCH", "aOtCH");

@temp = sort({ if ($1 eq "Juice") { return -1; } else if ($2 eq "Juice") { return 1; } return lc($1) cmp lc($2); }, @data);

map({ println("Value: $1"); }, @temp);
