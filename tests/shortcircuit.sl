sub test1 {
	if (($a && $b) || $c) {
		println("( $a && $b ) || $c is TRUE");
	}
	else {
		println("( $a && $b ) || $c is false");
	}

	if ($c || ($a && $b)) {
		println("$c || ( $a && $b ) is TRUE");
	}
	else {
		println("$c || ( $a && $b ) is false");
	}
}

for ($a = 0; $a <= 1; $a++) {
	for ($b = 0; $b <= 1; $b++) {
		for ($c = 0; $c <= 1; $c++) {
			test1(\$a, \$b, \$c);
		}
	}
}

sub test2 {
	if ((($a && $b) && $c) || $d) {
		println("( ( $a && $b ) && $c ) || $d is TRUE");
	}
	else {
		println("( ( $a && $b ) && $c ) || $d is false");
	}

	if ($d || (($a && $b) && $c)) {
		println("$d || ( ( $a && $b ) && $c ) is TRUE");
	}
	else {
		println("$d || ( ( $a && $b ) && $c ) is false");
	}
}

for ($a = 0; $a <= 1; $a++) {
	for ($b = 0; $b <= 1; $b++) {
		for ($c = 0; $c <= 1; $c++) {
			for ($d = 0; $d <= 1; $d++) {
				test2(\$a, \$b, \$c, \$d);
			}
		}
	}
}
