package com.example.demo.payment.model;

import java.sql.Timestamp;


public interface getJoinProducts {
    
    //temporders
    int getToid();
    Timestamp getTo_created();
    String getTo_mcht_trd_no();
    int getTo_price();
    String getTo_email();
    int getTo_cash();
    int getTo_point();
    //tempproducts
    int getTopid();
    int getTop_count();
    Timestamp getTop_created();
    String getTop_mcht_trd_no();
    String getTop_name();
    int getTop_price();
    String getTop_email();
    String gettop_usecode();
    String gettop_usecoupon();
    //tempreservations 추후 작업예정
    int getTrid();
    String getTr_mcht_trd_no();
    Timestamp getTrcreated();

}
