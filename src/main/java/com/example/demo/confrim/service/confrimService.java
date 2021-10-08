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
    private final int limitDay=1;
    private final int numLength=10;
    private final int noDoneNum=0;
    private final int doneNum=1;

    @Autowired
    private phoneDao phoneDao;
    
    @Transactional(rollbackFor = Exception.class)
    public JSONObject sendPhone(trySendSmsDto sendSmsDto) {
        System.out.println("sendPhone");
        phoneVo phoneVo=phoneDao.findByPhoneNum(sendSmsDto.getPhone()).orElseGet(() -> new phoneVo().builder().pcount(1).pcreated(Timestamp.valueOf(LocalDateTime.now())).phoneNum(sendSmsDto.getPhone()).build());
        System.out.println("문자메세지 전송"+phoneVo);
        sendRandNumInter sendRandNumInter=new sendPhoneInter(phoneVo.getPcount(),phoneVo.getPhoneNum(),phoneVo.getPcreated(),utillService.getRandomNum(numLength));
        sendRandNum(sendRandNumInter);
        return utillService.makeJson(true, "인증번호 전송");
    }
    private void sendRandNum(sendRandNumInter sendRandNumInter){
        System.out.println("sendRandNum");
        int reuqestCount=sendRandNumInter.getCount();
        LocalDateTime firstRequest=sendRandNumInter.getCreated().toLocalDateTime();
        try {
            if(reuqestCount==1){
                System.out.println("첫 인증번호 요청");
            }else if(firstRequest.isBefore(firstRequest.plusDays(limitDay))){
                System.out.println("첫 요청후 하루가 지나지않음");
                if(reuqestCount>maxReuqest){
                    System.out.println(maxReuqest+"회 이하입니다");

                    System.out.println("요청 db 수정완료");
                }else{
                    System.out.println("첫 요청 후 하루가 지나지않고 최대 회수 초과한 상태");
                    utillService.throwRuntimeEX(new RuntimeException(),"하루 최대 요청 횟수는 "+maxReuqest+"입니다","sendRandNum");
                }
                return; 
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
            phoneDao.save(interToVo(sendRandNumInter));
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
    private void update(sendPhoneInter sendPhoneInter){
        System.out.println("update");

    }
    private phoneVo interToVo(sendRandNumInter sendRandNumInter){
        System.out.println("interToVo");
        phoneVo vo=phoneVo.builder()
        .pcount(sendRandNumInter.getCount())
        .phoneNum(sendRandNumInter.getEmailOrPhone())
        .randNum(sendRandNumInter.getRandNum())
        .donePhone(noDoneNum)
        .build();
        return vo;
    }
}
