sub a {
	println("a");
	return true;
}

sub b {
	println("b");
	return true;
}

sub c {
	println("c");
	return false;
}

sub d {
	println("d");
	return true;
}

if (a() && b() && c()) {
	println("T1");
}
else {
	println("F1");
}

if (c() && a() && b()) {
	println("T2");
}
else {
	println("F2");
}

if (a() || b() || c()) {
	println("T3");
}
else {
	println("F3");
}

if (c() || a() || b()) {
	println("T4");
}
else {
	println("F4");
}

if ((a() && b() && c()) || d()) {
	println("T5");
}
else {
	println("F5");
}

if ((a() && c() && b()) || d()) {
	println("T6");
}
else {
	println("F6");
}

if (d() || (a() && c() && b())) {
	println("T7");
}
else {
	println("F7");
}

if (d() && (a() || c() || b())) {
	println("T8");
}
else {
	println("F8");
}
