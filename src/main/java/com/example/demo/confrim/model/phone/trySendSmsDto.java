package com.example.demo.confrim.model.phone;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class trySendSmsDto {

   @NotBlank(message = "빈칸을 채워주세요")
   private String unit; 
  
   @NotBlank(message = "스코프가 유효하지 않습니다")
   private String scope;

   @NotBlank(message = "디테일이 유효하지 않습니다")
   private String detail;
}
