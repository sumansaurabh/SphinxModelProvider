/*
 * OVFL.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */

#include<cstdio>

using namespace std;
int main()
{
    long long a,b,c,mid;
    scanf("%lld%lld%lld",&a,&b,&c);
    if(a<b&&a<c)
    	mid=a+b*c;
    else if(b<a&&b<c)
       	mid=b+a*c;
    else
    	mid=c+b*a;
    printf("%lld",mid);
}



