/*
 * lcs1.cpp
 *
 *  Created on: 15-Dec-2013
 *      Author: perfectus
 */
#include<iostream>
#include<string.h>
#define size 50000
using namespace std;
    char A[size],B[size];

    int i,j,A_len,B_len;
    int main()
    {
        cin>>B;
        cin>>A;
        int diag=0;
        A_len = strlen(A);
        B_len = strlen(B);
        int score[B_len];
        for(i=0;i<=A_len;i++)
        {
            for(j=0;j<=B_len;j++)
            {

                if(i==0 || j==0)
                    score[j]=0;
                else if(A[i-1] == B[j-1] )
                    score[j] = diag + 1;
                else
                {
                	diag=score[j];
                    if(score[j-1]>score[j])
                        score[j] = score[j-1];
                }
            }

        }

          cout<<score[B_len];
    }



