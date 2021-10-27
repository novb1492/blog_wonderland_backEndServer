package com.example.demo.apis.settle.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.enums.Stringenums;
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
    private final String cardMchtId=Stringenums.cardMchtId.getString();
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
    public JSONObject confrimPayment(HttpServletRequest request) {
        logger.info("confrimPayment");
        settleDto settleDto=utillService.requestToSettleDto(request);
        System.out.println(settleDto.toString());
        JSONObject reponse=new JSONObject();
        if(settleDto.getMchtId().equals(cardMchtId)){
            reponse=cardService.cardConfrim(settleDto);
        }else{
            return utillService.makeJson(false, "지원하지 않는 결제 형식입니다");
        }
        return reponse;
    }
}
