#include <cstdio>
#include <cstdlib>

void genLoop(FILE *file, int index, int n) {

	if (index == n)
		return;

	fprintf(file, "while (a%d < n) {\n", index);
	fprintf(file, "a%d = 0;\n", index + 1);
	genLoop(file, index + 1, n);
	fprintf(file, "a%d = a%d + 1;\n", index, index);
	fprintf(file, "}\n");
}

int main(int argc, char **argv) 
{
	if (argc < 2)
	{
		printf("%s [loop num]\n", argv[0]);
		return 0;
	}
	
	int n = atoi(argv[1]);

	char filename[20];
	sprintf(filename, "examples/loop%d.dart", n);

	FILE *file = fopen(filename, "w");
	fprintf(file, "void main()\n");
	fprintf(file, "{\n");

	fprintf(file, "int ");
	for (int i = 0; i < n; i++)
		fprintf(file, "a%d, ", i);
	fprintf(file, "a%d;\n", n);

	fprintf(file, "int n;\n");
	fprintf(file, "a0 = 0;\n");
	fprintf(file, "n = %d;\n", n);

	genLoop(file, 0, n);
	fprintf(file, "}\n");
	fclose(file);

	return 0;
}
