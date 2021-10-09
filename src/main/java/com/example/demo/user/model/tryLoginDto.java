package com.example.demo.user.model;

import javax.validation.constraints.NotBlank;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryLoginDto {
    
    @NotBlank(message = "이메일이 빈칸입니다")
    private String email;
    
    @NotBlank(message = "비밀번호가 빈칸입니다")
    private String pwd;
}
