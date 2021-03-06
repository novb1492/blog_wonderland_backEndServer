package com.example.demo.apis.settle.service;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.apis.requestTo;
import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.enums.Stringenums;
import com.example.demo.hash.aes256;
import com.example.demo.hash.sha256;
import com.example.demo.payment.model.card.paidCardsDao;
import com.example.demo.payment.model.card.paidCardsDto;
import com.example.demo.payment.service.paymentService;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class cardService {
    
    @Autowired
    private userService userService;
    @Autowired
    private requestTo requestTo;
    @Autowired
    private paymentService  paymentService;
    @Autowired
    private paidCardsDao paidCardsDao;


    private final String sucPayNum=Stringenums.sucPayNum.getString();
    private final String MchtId=Stringenums.cardMchtId.getString();
    
    private static Logger logger=LoggerFactory.getLogger(cardService.class);

    private JSONObject cancle(settleDto settleDto){
        logger.info("cancle");
        Map<String,String>map=utillService.getTrdDtTrdTm();
        String trdDt=map.get("trdDt");
        String trdTm=map.get("trdTm");
        String pktHash=requestcancleString(trdDt,trdTm,settleDto.getMchtTrdNo(),settleDto.getTrdAmt());
        logger.info(pktHash+" 해쉬예정문자열");
        JSONObject body=new JSONObject();
        JSONObject params=new JSONObject();
        JSONObject data=new JSONObject();
        params.put("mchtId", MchtId);
        params.put("ver", "0A18");
        params.put("method", "CA");
        params.put("bizType", "C0");
        params.put("encCd", "23");
        params.put("mchtTrdNo", settleDto.getMchtTrdNo());
        params.put("trdDt",trdDt);
        params.put("trdTm",trdTm);
        data.put("cnclOrd", settleDto.getCnclOrd());
        data.put("pktHash", sha256.encrypt(pktHash));
        data.put("orgTrdNo", settleDto.getTrdNo());
        data.put("crcCd", "KRW");
        data.put("cnclAmt", aes256.encrypt(settleDto.getTrdAmt()));
        body.put("params", params);
        body.put("data", data);
        
        return body;
    }
    private boolean requestToSettle(JSONObject body){
        HttpHeaders headers=requestTo.getHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("charset", "UTF-8");
        JSONObject  response=requestTo.requestToApi(body,"https://tbgw.settlebank.co.kr/spay/APICancel.do",headers);
        LinkedHashMap<String,Object>params=(LinkedHashMap<String, Object>) response.get("params");
        if(params.get("outStatCd").equals("0021")){
            return true;
        }
        return false;
    }
    private String requestcancleString(String trdDt,String trdTm, String mchtTrdNo,String price) {
        System.out.println("requestcancleString");
        return  String.format("%s%s%s%s%s%s",trdDt,trdTm,MchtId,mchtTrdNo,price,"ST1009281328226982205"); 
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject cardConfrim(settleDto settleDto) {
        logger.info("cardConfrim");
        JSONObject reseponse=new JSONObject();
        String mchtTrdNo=settleDto.getMchtTrdNo();
        try {
            settleDto.setTrdAmt(utillService.aesToNomal(settleDto.getTrdAmt()));
            if(paidCardsDao.countByPcMchtTrdNo(mchtTrdNo)!=0){
                return utillService.makeJson(true, "구매가 완료되었습니다");
            }
            if(!settleDto.getOutStatCd().equals(sucPayNum)){
                logger.info("결제실패 실패 코드 "+settleDto.getOutRsltCd());
                throw new RuntimeException("결제실패");
            }
            paymentService.confrimAndInsert(settleDto);
            paymentService.updateTemp(mchtTrdNo);
            insert(settleDto);
            reseponse.put("flag", true);
            reseponse.put("mchtTrdNo", mchtTrdNo);
            reseponse.put("price", settleDto.getTrdAmt());
           // return reseponse;
            throw new RuntimeException();
        } catch (Exception e) {
            settleDto.setCnclOrd(1);
            String message="환불에 실패했습니다";
            if(!e.getMessage().startsWith("메")){
                message="구매에 실패하였습니다";
            }
            if(requestToSettle(cancle(settleDto))){
                message=e.getMessage()+" 환불되었습니다";
            }
            throw utillService.makeRuntimeEX(message, "cardConfrim");
        }
        
    }
    private void insert(settleDto settleDto) {
        logger.info("insert");
        String email=userService.sendUserInfor().getEmail();
        int point=settleDto.getPoint();
        String mchtTrdNo=settleDto.getMchtTrdNo();
        paidCardsDto dto=paidCardsDto.builder()
                                    .pcCncl_ord(0)
                                    .pcEmail(email)
                                    .pcFn_nm(settleDto.getFnNm())
                                    .pcMchtTrdNo(mchtTrdNo)
                                    .pcMcht_id(settleDto.getMchtId())
                                    .pcMethod(settleDto.getMethod())
                                    .pcTrd_amt(Integer.parseInt(settleDto.getTrdAmt()))
                                    .pcTrd_no(settleDto.getTrdNo())
                                    .pcPoint(point)
                                    .pcCancleFlag(0)
                                    .build();
                                    paidCardsDao.save(dto);
                                    
    }

}
