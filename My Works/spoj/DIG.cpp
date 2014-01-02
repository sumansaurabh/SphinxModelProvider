/*
 * DIG.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */
#include<iostream>
using namespace std;
long long mod = 24*1000000007LL;
int main() {
	int t;
	long long n,ans,x,y,z;
	cin>>t;
	while(t--)
	{
		cin>>n;
		x = (n*(n-1)) % (mod);
		y = (x*(n-2)) % (mod);
		z = (y*(n-3)) % (mod);
		ans=z/24;
		cout<<ans<<endl;
	}
    return 0;
}

