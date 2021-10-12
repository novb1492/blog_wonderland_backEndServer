package com.example.demo.confrim.model.phone;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryConfrimRandNumDto {
    
    @NotBlank
    private String phoneOrEmail; 

    @NotBlank
    private String randNum;

    @NotBlank
    private String unit;

    @NotBlank
    private String scope;
}
