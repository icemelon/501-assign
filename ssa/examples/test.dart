void swap(int x, int y) {
	int t;
	int i, l;
	l = 0;
	i = 0;
	while (i < 10) {
		t = x;
		x = y;
		y = t;
		i = i + 1;
	}
}

void foo(int l) {
	int a, b, c, d, e, f;
	int u, v, w, x, y, z;
	u = a + b;
	v = c + d;
	w = e + f;

	if (l > 0) {
		x = c + d;
		y = c + d;
	} else {
		u = a + b;
		x = e + f;
		y = e + f;
	}
	z = u + y;
	u = b + a;
}

void test(int x) {
	int a, b, c, d;
	int y, t;
	a = 3;
	b = 7;
	c = 4;
	d = 7;
	t = 5;
	if (t > 10) {
		y = a + b;
	} else {
		y = c + d;
	}
}

void main() {
	int i;
	int a, b;
	i = 0;
	while (i < 10) {
		i = i + 1;
	}
	a = 1;
	b = 2;
	swap(a,3);
}

