package com.example.demo.enums;

public enum Stringenums {
    role_user("role_user"),
    flag("flag"),
    message("message"),
    data("data"),
    statusPaid("paid"),
    statusReady("ready"),
    reservation("reservation"),
    tooSmall("small"),
    tooBig("big"),
    collect("collect"),
    find("find"),
    confrim("confrim"),
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
