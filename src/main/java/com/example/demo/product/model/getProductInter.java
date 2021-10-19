package com.example.demo.product.model;

import java.sql.Timestamp;



public interface getProductInter {
    int getPid();
    int getPrice();
    int getCount();
    String getProduct_name();
    String getKind();
    String getProduct_img();
    Timestamp getPcreated();
    int getTotalcount();
}
