package com.example.demo.user.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryJoinDto {
    
    @NotBlank(message = "이메일이 빈칸입니다")
    private String email;

    @Size
    @NotBlank(message = "비밀번호가 빈칸입니다")
    private String pwd;

    @NotBlank(message = "비밀번호가 빈칸입니다")
    private String pwd2;
    @NotBlank(message = "이름이 빈칸입니다")
    private String name;
    @NotBlank(message = "우편번호가 빈칸입니다")
    private String postcode;
    @NotBlank(message = "주소가 빈칸입니다")
    private String address;
    @NotBlank(message = "상세주소가 빈칸입니다")
    private String detailAddess;
    
    @Size(min = 11,max = 11,message = "핸드폰 번호를 확인해 주세요")
    @NotBlank(message = "전화번호가 빈칸입니다")
    private String phone;
}
