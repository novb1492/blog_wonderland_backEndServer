package com.example.demo.confrim.model;

import java.sql.Timestamp;

public interface sendRandNumInter {
    String getUnit();
    int getCount();
    String getEmailOrPhone();
    Timestamp getCreated();
    String getRandNum();

}
