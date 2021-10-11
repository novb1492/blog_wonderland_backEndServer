package com.example.demo.apis.coolsms;

import java.util.HashMap;

import com.example.demo.utill.utillService;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

public class sendMessage {
    private static final String apikey="NCSFT0AZ2O3FHMAX";
    private static final String apiSecret="AHZNZ3IIMGSYIXFLR7HQDBYA5KPFSFCS";
    private static final String companyPhone="01091443409";

    public static void sendMessege(String phoneNum,String messege) {
       System.out.println(phoneNum+" 문자전송번호");
        Message coolsms = new Message(apikey, apiSecret);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNum);
        params.put("from", companyPhone);
        params.put("type", "SMS");
        params.put("text", messege);
        try {
            coolsms.send(params);
            System.out.println("문자 전송 완료");
        } catch (CoolsmsException e) {
           utillService.throwRuntimeEX(e,e.getMessage(), "sendMessege");
            
        }catch (Exception e) {
            utillService.throwRuntimeEX(e,"인증번호 전송 실패", "sendMessege");
             
         }
    
    }
}
