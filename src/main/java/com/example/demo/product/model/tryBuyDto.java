package com.example.demo.product.model;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryBuyDto {
    
    
    @NotBlank(message = "구매방법을 선택해주세요")
    private String buyKind;

    @Size(min = 1,message = "구매 수량을 선택해주세요")
    private Object[][]buy;

    @NotBlank(message = "품목이 잘못되었습니다")
    private String kind;

    private String point;

   
}
