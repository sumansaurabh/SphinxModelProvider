/*
 * NGM.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */
#include<iostream>
using namespace std;
int main()
{
	int n,a;
	cin>>n;
	a=n%10;
	if(a)
		cout<<1<<endl<<a;
	else
		cout<<2;
}


