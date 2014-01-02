/*
 * GPACALC.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */

#include<iostream>
#include<iomanip>
using namespace std;
int main()
{
	int t,s,cr,pos;
	char ls[2];
	cin>>t;
	int marks[5]={9,8,7,6,5};

	while(t--)
	{
		cin>>s;
		int sum =0;int tot=0;
		while(s--)
		{
			cin>>ls[0];
			cin>>ls[1];
			cr=(int)ls[0]-48;
			pos=(int)ls[1]-65;

			if(ls[1]=='S')
				sum+=cr*10;
			else
				sum+=cr*marks[pos];
			tot+=cr;
		}
		cout<<fixed<<setprecision(2);
		cout<<(float)sum/tot<<endl;
	}
}
