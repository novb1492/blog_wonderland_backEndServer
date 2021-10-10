package com.example.demo.confrim.model.phone;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class trySendSmsDto {

   private String phone; 
   private String email;

   @NotBlank
   private String scope;
}
