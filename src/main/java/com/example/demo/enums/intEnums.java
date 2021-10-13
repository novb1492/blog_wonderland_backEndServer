package com.example.demo.enums;

public enum intEnums {
    pwdMin(4),
    pwdMax(10),
    doneNum(1),
    randNumLength(10),
    maxRequest(10),
    limitMin(3),
    noDoneNum(0);



    private int num;

    intEnums(int num){
        this.num=num;
    
    }
    public int getInt() {
        return num;
    }    
}
