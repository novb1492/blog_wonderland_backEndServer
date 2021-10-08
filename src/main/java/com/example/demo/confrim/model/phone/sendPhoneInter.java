package com.example.demo.confrim.model.phone;

import java.sql.Timestamp;

import com.example.demo.confrim.model.sendRandNumInter;

public class sendPhoneInter implements sendRandNumInter {

    private int count;
    private String addressOrNum;
    private Timestamp firstReuqest;
    private String randNum;

    public sendPhoneInter(int count,String addressOrNum,Timestamp firstReuqest,String randNum){
        this.count=count;
        this.addressOrNum=addressOrNum;
        this.firstReuqest=firstReuqest;
        this.randNum=randNum;
    }
    @Override
    public String getUnit() {
        return "phone";
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
    
}