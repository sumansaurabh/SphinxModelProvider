/*
 * CENCRY.cpp
 *
 *  Created on: 21-Dec-2013
 *      Author: perfectus
 */
#include<cstdio>
#include<string.h>
using namespace std;
int main()
{
	int T,pos,loc;
	scanf("%d",&T);
	char str[50000],vowel[]={"aeiou"},consonant[]={"bcdfghjklmnpqrstvwxyz"};
	int hash[26]={0,0,1,2,1,3,4,5,2,6,7,8,9,10,3,11,12,13,14,15,4,16,17,18,19,20};
	while(T--)
	{
		int hashC[26]={0};
		scanf("%s",str);
		for(int i=0;i<strlen(str);i++)
		{
			int ch=str[i]-97;
			pos=hash[ch];
			loc=hashC[ch];
			switch(ch)
			{
				case 0:
				case 4:
				case 8:
				case 14:
				case 20:
					printf("%c",consonant[(loc*5+pos)%21]);
					break;
				default:
					printf("%c",vowel[(loc*21+pos)%5]);
					break;
			}
			hashC[ch]++;
		}
		printf("\n");
	}
	return 0;
}



