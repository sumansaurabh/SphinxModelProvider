/*
 * AE00.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */


#include<iostream>
using namespace std;
int main (){
int n,i,j;
long sum;
    cin>>n;
    sum=0;
    for(i=1;i<=n;i++)
    {
    for (j=1;j*j<=i;++j)
      if (i%j==0) ++sum;
    }
cout<<sum;
return 0;
}

