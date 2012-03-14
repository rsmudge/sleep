%a = %();

%a['a'] = %a;
%a['b'] = %b;
%b['d'] = "dog";
%b['a'] = %a;
%b['b'] = %b;
%c      = %(d => "cat");
%c['a'] = %a;
%c['b'] = %b;
%c['c'] = %c;
%a['c'] = %c;

println(%a);
