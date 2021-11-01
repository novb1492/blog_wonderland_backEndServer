package com.example.demo.apis.settle.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.enums.Stringenums;
import com.example.demo.hash.aes256;
import com.example.demo.hash.sha256;
import com.example.demo.payment.service.paymentService;
import com.example.demo.product.model.tryBuyDto;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class settleService {

    private static Logger logger=LoggerFactory.getLogger(settleService.class);
    private final String cardMchtId=Stringenums.cardMchtId.getString();
    private final String vbankMchtId=Stringenums.vbanMchtId.getString();

    @Autowired
    private cardService cardService;
    @Autowired
    private paymentService paymentService;
    @Autowired
    private userService userService;

    @Transactional(rollbackFor = Exception.class)
    public JSONObject makeBuyInfor(tryBuyDto tryBuyDto,List<Map<String,Object>>maps) {
        Map<String,Object>map=maps.get(maps.size()-1);
        Map<String,String>trdDtTrdTm=utillService.getTrdDtTrdTm();
        String mchtTrdNo=maps.get(0).get("bigKind")+utillService.getRandomNum(10);
        String requestDate=trdDtTrdTm.get("trdDt");
        String requestTime=trdDtTrdTm.get("trdTm");
        String totalCash=Integer.toString((int)map.get("totalCash"));
        String buyKind=tryBuyDto.getBuyKind();
        String[] idAndText=getRequestTest(buyKind, mchtTrdNo, requestDate, requestTime, totalCash);
        String settleText=idAndText[1];
        logger.info(settleText+" 해쉬예정문자열");
        String hashText=sha256.encrypt(settleText);
        logger.info(hashText+" 해쉬문자열");
        String priceHash=aes256.encrypt(totalCash);
        JSONObject response=new JSONObject();
        String  email=userService.sendUserInfor().getEmail();
        if(buyKind.equals("vbank")){
            logger.info("가상계좌 만료시간 담기");
            response.put("expireDt", map.get("expireDate"));
        }
        response.put("itemName", map.get("itemNames"));
        response.put("mchtId", idAndText[0]);
        response.put("mchtCustId", aes256.encrypt(email));
        response.put("mchtTrdNo", mchtTrdNo);
        response.put("trdAmt", priceHash);
        response.put("trdDt", requestDate);
        response.put("trdTm", requestTime);
        response.put("pktHash", hashText);
        response.put("flag", true);
        paymentService.insertTemp(mchtTrdNo,email,tryBuyDto.getBuyKind(),(int)map.get("totalCash"),(int)map.get("totalPoint"));
        paymentService.insertTemp(maps, mchtTrdNo, email);
        return response;
    }
    private String[] getRequestTest(String method,String mchtTrdNo,String requestDate,String requestTime,String totalCash) {
        logger.info("getRequestTest");
        String[] idAndText=new String[2];
        String id="";
        if(method.equals("card")){
            id=cardMchtId;
        }else if(method.equals("vbank")){
            id=vbankMchtId;
        }else{
            throw utillService.makeRuntimeEX("지원하지 않는 결제 방식입니다", "getRequestTest");
        }
        idAndText[0]=id;
        idAndText[1]=utillService.getSettleText(id,method, mchtTrdNo, requestDate, requestTime, totalCash);
        return idAndText;
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
