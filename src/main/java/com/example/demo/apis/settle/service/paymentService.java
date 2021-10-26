package com.example.demo.apis.settle.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.product.model.tryBuyDto;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class paymentService {
    private static Logger logger=LoggerFactory.getLogger(paymentService.class);
    @Autowired
    private cardService cardService;

    public JSONObject makeBuyInfor(tryBuyDto tryBuyDto,Map<String,Object>map) {
        logger.info("makeBuyInfor");
        String buyKind=tryBuyDto.getBuyKind();
        if(buyKind.equals("card")){
           return cardService.makeInfor(tryBuyDto, map);
        }else if(buyKind.equals("vbank")){
            return null;
        }else if(buyKind.equals("kakaoPay")){
            return null;
        }else{
            return utillService.makeJson(false, "지원하지 않는 결제수단입니다");
        }
    }
    
    
}
