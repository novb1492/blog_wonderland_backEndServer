package com.example.demo.confrim.model.phone;

import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class trySendSmsDto {
   @Size(min = 11,max = 11,message = "핸드폰번호를 확인해주세요")
   private String phone; 
}
