package com.example.demo.user.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryUpadateDto {
    
    
    private String pwd;
    private String pwd2;
    private String postcode;
    private String address;
    private String detailAddress;
    private String scope;
    private String token;
    private String detail;
}
