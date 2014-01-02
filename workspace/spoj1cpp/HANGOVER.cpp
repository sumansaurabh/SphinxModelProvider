/*
 * HANGOVER.cpp
 *
 *  Created on: 23-Dec-2013
 *      Author: perfectus
 */
#include <iostream>
using namespace std;
int main()
{
	double c,tot=0.0;
	int i;
	while(1)
	{
		cin>>c;
		if(c==0.0)
			break;
		for(i=1;tot<=c;++i)
			tot += 1.0/(i+1.0);
		  cout<<i<<" card(s)"<<endl;
	}
}


