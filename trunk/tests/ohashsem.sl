$default = 42;
%hash = ohash();
setMissPolicy(%hash, lambda({ return $default; }, \$default));

%hash["uh"] += 4;
%hash["eh"] += 7;


println(%hash);
