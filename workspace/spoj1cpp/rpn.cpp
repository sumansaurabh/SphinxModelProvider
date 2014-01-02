

#include <stdio.h>
#include <string.h>


#define STACKSIZE 1000 /* max size of the stack */


int main(void)
{
    int iters; /* number of test cases */
    char opstack[STACKSIZE];

    scanf("%d", &iters);
    while (iters--)
    {
        int i;
        int offset = 0; /* offset of the stack */
        char caes[STACKSIZE]; /* input expression */
        scanf("%s", caes);

        for (i=0; i<(int)strlen(caes); i++) /* run for all characters */
        { /* in the expression */
            char c = caes[i];

            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') /* push operator in stack */
                opstack[offset++] = c;

            else if (c == ')') /* pop one from stack */
                printf("%c", opstack[--offset]);

            else if (c >= 'a' && c <= 'z')
                printf("%c", c);
        }
        printf("\n"); /* end the expression */
    }
    return 0;
}
