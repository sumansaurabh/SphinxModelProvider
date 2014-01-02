/*
 * cmpls.cpp
 *
 *  Created on: 13-Dec-2013
 *      Author: perfectus
 */


#include<iostream>
#include<stdio.h>
using namespace std;


int main(){
    int T;
    cin>>T;
    for(int i=0;i<T;i++){
        int S,C;
        cin>>S>>C;
        int A[S],B[S];
        double s;
        for(int i=0;i<S;i++) scanf("%d",&A[i]);
        bool flag=1;
        int size=S,j=0;
        for(int i=0;i<S;i++) B[i]=A[i];
        while(flag){
            flag=0;
            for(int i=0;i<size;i++) if(B[0]!=B[i]) flag=1;
            if(flag==0) break;
            for(int i=0;i<size-1;i++) B[0]=B[1]-B[0];
            size--;j++;
        }
        cout<<j<<endl;
        cout<<endl;
    }
    return 0;
}

