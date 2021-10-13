package com.example.demo.send;

import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class sendService {

    @Autowired
    private sendPhoneService sendPhoneService;
    @Autowired
    private sendEmailService sendEmailService;
        
    public JSONObject sendNum(trySendSmsDto trySendSmsDto) {
        System.out.println("sendNum");
        if(trySendSmsDto.getScope().equals("phone")){
            System.out.println("문자 인증번호 전송 요청");
            sendPhoneService.sendPhone(trySendSmsDto); 
        }else if(trySendSmsDto.getScope().equals("email")){
            System.out.println("이메일 인증번호 전송 요청");
            sendEmailService.sendEmail(trySendSmsDto);
        }else{
            return utillService.makeJson(false, "수단이 유효하지 않습니다");
        }
        return utillService.makeJson(true, "인증번호 전송이 완료 되었습니다");
    }

}
