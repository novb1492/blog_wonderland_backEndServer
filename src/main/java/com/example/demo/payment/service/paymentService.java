package com.example.demo.payment.service;

import java.util.Map;


import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.apis.settle.service.cardService;
import com.example.demo.enums.Stringenums;
import com.example.demo.payment.model.tempOrderDao;
import com.example.demo.payment.model.tempOrderDto;
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
    private final String cardMchtId=Stringenums.cardMchtId.getString();
    @Autowired
    private cardService cardService;
    @Autowired
    private  tempOrderDao tempOrderDao;

    /*public JSONObject makeBuyInfor(tryBuyDto tryBuyDto,Map<String,Object>map) {
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
    }*/
    public JSONObject confrimPay(settleDto settleDto) {
        logger.info("confrimPay");
        JSONObject response=new JSONObject();
        if(settleDto.getMchtId().equals(cardMchtId)){
            logger.info("카드결제 검증");
            response=cardService.confrim(settleDto);
        }
        return response;
    }
    public  void insertTemp(String mchtTrdNo,int price,String email) {
        System.out.println(mchtTrdNo+price+email);
        logger.info("insertTemp");
        tempOrderDto  dto=tempOrderDto.builder()
                                        .toMchtTrdNo(mchtTrdNo)
                                        .toPrice(price)
                                        .toemail(email)
                                        .build();
                                        tempOrderDao.save(dto);
    } 
    public void insertTempOrderProducts() {
        logger.info("insertTempOrderProducts");
    }  
    
    
}
