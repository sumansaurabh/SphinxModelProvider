/*
 * KAMIL.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */

#include<iostream>
using namespace std;
int main(){
	char c[20];
	int X=1,n=10;
	while(n--)
	{
		cin>>c;

		if(c=='F'||c=='L'||c=='D'||c=='T')
			X*=2;
		cout<<endl;
	}
}


