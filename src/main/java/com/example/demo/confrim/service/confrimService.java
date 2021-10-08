package com.example.demo.confrim.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.example.demo.confrim.model.sendRandNumInter;
import com.example.demo.confrim.model.phone.phoneDao;
import com.example.demo.confrim.model.phone.phoneVo;
import com.example.demo.confrim.model.phone.sendPhoneInter;
import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class confrimService {

    private final int maxReuqest=10;

    @Autowired
    private phoneDao phoneDao;
    
    @Transactional(rollbackFor = Exception.class)
    public JSONObject sendPhone(trySendSmsDto sendSmsDto) {
        System.out.println("sendPhone");
        phoneVo phoneVo=phoneDao.findByPhoneNum(sendSmsDto.getPhone()).orElseGet(() -> new phoneVo().builder().pcount(0).pcreated(Timestamp.valueOf(LocalDateTime.now())).phoneNum(sendSmsDto.getPhone()).build());
        System.out.println("문자메세지 전송"+phoneVo);
        sendRandNumInter sendRandNumInter=new sendPhoneInter(phoneVo.getPcount(),phoneVo.getPhoneNum(),phoneVo.getPcreated());
        sendRandNum(sendRandNumInter);
        return utillService.makeJson(true, "인증번호 전송");
    }
    private void sendRandNum(sendRandNumInter sendRandNumInter){
        System.out.println("sendRandNum");
        int reuqestCount=sendRandNumInter.getCount();
        LocalDateTime firstRequest=sendRandNumInter.getCreated().toLocalDateTime();

        try {
            if(reuqestCount==0){
                System.out.println("첫 인증번호 요청");
            }else if(firstRequest.isAfter(LocalDateTime.now())){
                System.out.println("첫 요청후 하루가 지나지않음");
                
            }else{
                System.out.println("첫 요청후 하루가 지남");
                delete(sendRandNumInter);
                System.out.println("전날 요청 기록 삭제완료");
            }
            insert(sendRandNumInter);
        }catch (RuntimeException e) {
            utillService.throwRuntimeEX(e,e.getMessage(), "sendRandNum");
        }catch (Exception e) {
            utillService.throwRuntimeEX(e,"인증번호 전송 실패", "sendRandNum");
        }
    }
    private void insert(sendRandNumInter sendRandNumInter){
        System.out.println("insert");
        if(sendRandNumInter.getUnit().equals("phone")){
            System.out.println("전화인증 db 저장");
            phoneVo vo=phoneVo.builder()
                                .pcount(1)
                                .phoneNum(sendRandNumInter.getEmailOrPhone())
                                .build();
                                phoneDao.save(vo);
        }
    }
    private void delete(sendRandNumInter sendRandNumInter){
        System.out.println("delete");
        if(sendRandNumInter.getUnit().equals("phone")){
            System.out.println("핸드폰 인증 정보 삭제");
            phoneDao.deleteByPhoneNum(sendRandNumInter.getEmailOrPhone());
            return;
        }
    }
}
