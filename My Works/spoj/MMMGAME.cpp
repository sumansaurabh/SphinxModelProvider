/*
 * MMMGAME.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */
#include<iostream>
#include<cstring>
#include<cstdio>
using namespace std;
int main()
{
    int t,n,a[47],flag,sum;
    scanf("%d",&t);
    while(t--)
    {
        sum=0;
        flag=0;
        scanf("%d",&n);
        for(int i=0;i<n;i++)
        {
        	scanf("%d",&a[i]);
        	sum+=a[i];
        }
        if(sum==n)
        {
        	if(sum%2)
        		cout<<"Brother"<<endl;
            else
        		cout<<"John"<<endl;

         }
         else
         {
        	 for(int i=0;i<n;i++)
        		 flag=flag^a[i];
             if(flag)
                 cout<<"John"<<endl;
           else
        	   cout<<"Brother"<<endl;
          }
    }
}



