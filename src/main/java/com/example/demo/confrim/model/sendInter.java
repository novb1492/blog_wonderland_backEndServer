package com.example.demo.confrim.model;

import java.sql.Timestamp;

public class sendInter implements sendRandNumInter {

    private int count;
    private String addressOrNum;
    private Timestamp firstReuqest;
    private String randNum;
    private int doneOrNot;
    private String Scope;

    public sendInter(int count,String addressOrNum,Timestamp firstReuqest,String randNum,int doneOrNot,String Scope){
        this.count=count;
        this.addressOrNum=addressOrNum;
        this.firstReuqest=firstReuqest;
        this.randNum=randNum;
        this.doneOrNot=doneOrNot;
        this.Scope=Scope;
    }
    @Override
    public String getScope() {
        return Scope;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getEmailOrPhone() {
        return addressOrNum;
    }

    @Override
    public Timestamp getCreated() {
        return firstReuqest;
    }
    @Override
    public String getRandNum() {
        return randNum;
    }
    @Override
    public int doneOrNot() {
        return doneOrNot;
    }
    
}