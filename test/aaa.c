#include<stdio.h>
#include<stdlib.h>

int main(){
 	int *p = malloc(4);
	*p = 1;
	printf("%d",*p);
	return 0;
}
