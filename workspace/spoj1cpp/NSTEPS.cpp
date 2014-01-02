/*
 * NSTEPS.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */

#include<iostream>
using namespace std;
int main (){
	int n,x,y;
	cin>>n;
	while(n--)
	{
		cin>>x;
		cin>>y;
		int a=x%2;
		if(x==y||x==y+2)
		{
			if(a)
				cout<<x+y-1<<endl;
			else
				cout<<x+y<<endl;
		}
		else
			cout<<"No Number"<<endl;
	}
}


