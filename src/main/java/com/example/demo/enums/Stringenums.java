package com.example.demo.enums;

public enum Stringenums {
    flag("flag"),
    message("message"),
    data("data"),
    statusPaid("paid"),
    statusReady("ready"),
    reservation("reservation"),
    food("food"),
    product("product"),
    kakaoPay("kakaoPay"),
    vbank("vbank"),
    cardmehtod("nxca_jt_il"),
    vbankmehthod("nx_mid_il");


    private String messege;

    Stringenums(String messege){
        this.messege=messege;
    
    }
    public String getString() {
        return messege;
    }
}
