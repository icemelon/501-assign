void swap(int x, int y) {
	int t;
	int i;
	i = 0;
	while (i < 10) {
		t = x;
		x = y;
		y = t;
		i = i + 1;
	}
}

void test(int x) {
	int a, b, c, d;
	int y, t;
	a = 3;
	b = 7;
	c = 4;
	d = 7;
	t = 5;
	if (t < 10) {
		y = a + b;
	} else {
		y = c + d;
	}
}

void foo() {
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
	swap(a,b);
}

