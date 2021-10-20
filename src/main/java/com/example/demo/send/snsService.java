package com.example.demo.send;

import com.example.demo.confrim.model.phone.tryConfrimRandNumDto;
import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class snsService {
    private final static Logger LOGGER=LoggerFactory.getLogger(snsService.class);

    @Autowired
    private phoneService sendPhoneService;
    @Autowired
    private emailService sendEmailService;
        
    public JSONObject sendNum(trySendSmsDto trySendSmsDto) {
        LOGGER.info("sendNum");
        if(trySendSmsDto.getScope().equals("phone")){
            LOGGER.info("문자 인증번호 전송 요청");
            sendPhoneService.sendPhone(trySendSmsDto); 
        }else if(trySendSmsDto.getScope().equals("email")){
            LOGGER.info("이메일 인증번호 전송 요청");
            sendEmailService.sendEmail(trySendSmsDto);
        }else{
            return utillService.makeJson(false, "수단이 유효하지 않습니다");
        }
        return utillService.makeJson(true, "인증번호 전송이 완료 되었습니다");
    }
    public JSONObject checkRandNum(tryConfrimRandNumDto tryConfrimRandNumDto){
        LOGGER.info("checkRandNum");
        String errorMessage="인증번호 검증 오류";
        try {
            String unit=tryConfrimRandNumDto.getUnit();
            if(unit.equals("phone")){
                return sendPhoneService.checkNum(tryConfrimRandNumDto);
            }else if(unit.equals("email")){
                return sendEmailService.checkNum(tryConfrimRandNumDto);
            }else{
                return utillService.makeJson(false, "지원하지 않는 sns종류입니다");
            }
            
        }catch (RuntimeException e) {
            errorMessage=e.getMessage();
        }
        return utillService.makeJson(false, errorMessage);
    }
}
