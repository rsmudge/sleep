%hash = %(a => "baseball",
          b => "ardvarks",
          c => "cats"
          d => "dogs");

println(%hash);

printAll(map({ return typeOf($1) . " $1"; }, values(%hash)));
