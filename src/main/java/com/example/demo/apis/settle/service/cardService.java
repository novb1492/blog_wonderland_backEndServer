package com.example.demo.apis.settle.service;

import java.util.Map;

import com.example.demo.product.model.tryBuyDto;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class cardService {
    
    private static Logger logger=LoggerFactory.getLogger(cardService.class);

    public JSONObject makeInfor(tryBuyDto tryBuyDto,Map<String,Object>map) {
        logger.info("makeInfor");
        Map<String,String>trdDtTrdTm=utillService.getTrdDtTrdTm();
        String mchtTrdNo=utillService.getRandomNum(10);
        String requestDate=trdDtTrdTm.get("trdDt");
        String requestTime=trdDtTrdTm.get("trdTm");
        String totalPrice=Integer.toString((int)map.get("totalPrice"));
        String settleText=utillService.getSettleText("nxca_jt_il", "card", mchtTrdNo, requestDate, requestTime, totalPrice);
        logger.info(settleText+" 해쉬예정문자열");
        
        return null;
    }

}
