package com.example.demo.user.model;


import javax.validation.constraints.NotBlank;

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
    @NotBlank(message = "스코프가 빈칸입니다")
    private String scope;
    private String token;
    private String detail;
}
