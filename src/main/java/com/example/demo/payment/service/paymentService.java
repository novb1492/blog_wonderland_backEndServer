package com.example.demo.payment.service;



import java.util.List;
import java.util.Map;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.apis.settle.service.cardService;
import com.example.demo.enums.Stringenums;
import com.example.demo.payment.model.tempOrderDao;
import com.example.demo.payment.model.tempOrderDto;
import com.example.demo.payment.model.tempOrderProudctsDao;
import com.example.demo.payment.model.tempOrderProudctsDto;
import com.example.demo.product.model.tryBuyDto;
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
    @Autowired
    private tempOrderProudctsDao tempOrderProudctsDao;


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
    public void insertTempOrderProducts(List<Map<String,Object>>maps,String mchtTrdNo,String email) {
        logger.info("insertTempOrderProducts");
        int temp=0;
        int size=maps.size();
        for(Map<String,Object> m:maps){
            if(temp>=size-1){
                break;
            }
            tempOrderProudctsDto dto=tempOrderProudctsDto.builder()
                                                        .topCount((int)m.get("count"))
                                                        .topMchtTrdNo(mchtTrdNo)
                                                        .topName((String)m.get("itemName"))
                                                        .topPrice((int)m.get("price"))
                                                        .topemail(email)
                                                        .build();
                                                        tempOrderProudctsDao.save(dto);
                                                        temp+=1;
        }
    }  
    
    
}
