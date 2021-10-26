package com.example.demo.apis.settle.service;

import java.util.Map;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.enums.Stringenums;
import com.example.demo.hash.aes256;
import com.example.demo.hash.sha256;
import com.example.demo.product.model.tryBuyDto;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class cardService {
    
    @Autowired
    private userService userService;

    private final String sucPayNum=Stringenums.sucPayNum.getString();
    private final String MchtId=Stringenums.cardMchtId.getString();
    private static Logger logger=LoggerFactory.getLogger(cardService.class);

    public JSONObject makeInfor(tryBuyDto tryBuyDto,Map<String,Object>map) {
        logger.info("makeInfor");
        Map<String,String>trdDtTrdTm=utillService.getTrdDtTrdTm();
        String mchtTrdNo=utillService.getRandomNum(10);
        String requestDate=trdDtTrdTm.get("trdDt");
        String requestTime=trdDtTrdTm.get("trdTm");
        String totalPrice=Integer.toString((int)map.get("totalPrice"));
        String settleText=utillService.getSettleText(MchtId,"card", mchtTrdNo, requestDate, requestTime, totalPrice);
        logger.info(settleText+" 해쉬예정문자열");
        String hashText=sha256.encrypt(settleText);
        logger.info(hashText+" 해쉬문자열");
        String priceHash=aes256.encrypt(totalPrice);
        JSONObject response=new JSONObject();
        String  email=userService.sendUserInfor().getEmail();
        response.put("itemName", map.get("itemName"));
        response.put("mchtId", MchtId);
        response.put("mchtCustId", aes256.encrypt(email));
        response.put("mchtTrdNo", mchtTrdNo);
        response.put("trdAmt", priceHash);
        response.put("trdDt", requestDate);
        response.put("trdTm", requestTime);
        response.put("pktHash", hashText);
        response.put("flag", true);
        return response;
    }
    public JSONObject confrim(settleDto settleDto) {
        logger.info("confrim");
        try {
            if(!settleDto.getOutStatCd().equals(sucPayNum)){
                logger.info("결제실패 실패 코드 "+settleDto.getOutRsltCd());
                throw new RuntimeException("결제실패");
            }
            return utillService.makeJson(true, "구매가 완료되었습니다");
        } catch (Exception e) {
            if(cancle(settleDto)){
                return utillService.makeJson(false, "구매에 실패하였습니다");
            }
            return utillService.makeJson(false, "환불에 실패하였습니다");
        }
        
    }
    private boolean cancle(settleDto settleDto){
        logger.info("cancle");
        return true;
    }

}
