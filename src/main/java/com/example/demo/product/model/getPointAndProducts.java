package com.example.demo.product.model;

import java.sql.Timestamp;

public interface getPointAndProducts {
    
    //상품정보
    int getPrice();
    String getProduct_name();
    String getBig_kind();
    int getCount();
    int getMax_discount_percent();
    /*//쿠폰정보
    int getUsed_flag();
    Timestamp getCo_expired();
    String getCo_kind();
    int getCo_num();
    String getCoupon_name();
    //코드정보
    String getCode_name();
    String getCd_kind();
    int getCd_num();
    int getCd_using();
    Timestamp getCd_expired();*/
    //포인트정보
    int getPo_having();
    Timestamp getPo_expired();

}
