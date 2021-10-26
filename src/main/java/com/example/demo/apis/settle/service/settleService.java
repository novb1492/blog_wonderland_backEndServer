package com.example.demo.apis.settle.service;

import java.util.List;
import java.util.Map;

import com.example.demo.product.model.tryBuyDto;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class settleService {

    private static Logger logger=LoggerFactory.getLogger(settleService.class);
    @Autowired
    private cardService cardService;


    public JSONObject makeBuyInfor(tryBuyDto tryBuyDto,List<Map<String,Object>>maps) {
        logger.info("makeBuyInfor");
        String buyKind=tryBuyDto.getBuyKind();
        if(buyKind.equals("card")){
           return cardService.makeInfor(tryBuyDto,maps);
        }else if(buyKind.equals("vbank")){
            return null;
        }else{
            return utillService.makeJson(false, "지원하지 않는 결제수단입니다");
        }
    }
}
