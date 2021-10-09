package com.example.demo.enums;

public enum intEnums {
    pwdMin(4),
    pwdMax(10);



    private int num;

    intEnums(int num){
        this.num=num;
    
    }
    public int getString() {
        return num;
    }    
}
