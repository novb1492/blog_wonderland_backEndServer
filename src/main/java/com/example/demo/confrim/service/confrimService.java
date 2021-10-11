package com.example.demo.confrim.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.demo.confrim.model.sendInter;
import com.example.demo.confrim.model.sendRandNumInter;
import com.example.demo.confrim.model.email.emailDao;
import com.example.demo.confrim.model.email.emailVo;
import com.example.demo.confrim.model.email.getUserJoinInter;
import com.example.demo.confrim.model.phone.phoneDao;
import com.example.demo.confrim.model.phone.phoneVo;
import com.example.demo.confrim.model.phone.tryConfrimRandNumDto;
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
    @Autowired
    private emailDao emailDao;
    
    public JSONObject sendNum(trySendSmsDto trySendSmsDto) {
        System.out.println("sendNum");
        if(trySendSmsDto.getScope().equals("phone")){
            System.out.println("문자 인증번호 전송 요청");
            sendPhone(trySendSmsDto); 
        }else if(trySendSmsDto.getScope().equals("email")){
            System.out.println("이메일 인증번호 전송 요청");
            sendEmail(trySendSmsDto);
        }
        return utillService.makeJson(true, "인증번호 전송이 완료 되었습니다");
    }
    @Transactional(rollbackFor = Exception.class)
    public void sendEmail(trySendSmsDto trySendSmsDto) {
        System.out.println("sendEmail");
        System.out.println(trySendSmsDto.getUnit());
    
        emailVo emailVo=new emailVo();
        if(trySendSmsDto.getDetail().equals("confrim")){
            System.out.println("회원가입시 이메일 요청");
        }else{
            System.out.println("이미 회원가입 되어있는 이메일 찾기");
            getUserJoinInter getUserJoinInter=emailDao.findByEemailJoinUsers(trySendSmsDto.getUnit());
            if(getUserJoinInter.getAlready()!=1){
                System.out.println("회원가입 되어있지 않은 이메일");
                throw new RuntimeException("회원가입 되어있지 않은 이메일입니다");
            }else if(Optional.ofNullable(getUserJoinInter.getEemail()).orElseGet(()->"emthy").equals("emthy")){
                System.out.println("첫요청");
                emailVo.setDoneemail(0);
                emailVo.setEcount(0);
                emailVo.setEcreated(Timestamp.valueOf(LocalDateTime.now()));
            }else{
                System.out.println("요청 내역존재");
                emailVo.setEid(getUserJoinInter.getEid());
                emailVo.setDoneemail(getUserJoinInter.getDoneemail());
                emailVo.setEcount(getUserJoinInter.getEcount());
                emailVo.setEcreated(getUserJoinInter.getEcreated());
            }
            emailVo.setEemail(trySendSmsDto.getUnit());
        }
        System.out.println("이메일 전송"+emailVo);
        sendRandNumInter sendRandNumInter=new sendInter(emailVo.getEcount(),emailVo.getEemail(),emailVo.getEcreated(), utillService.getRandomNum(numLength),emailVo.getDoneemail(),trySendSmsDto.getScope());
        sendRandNum(sendRandNumInter);
        //sendMessage.sendMessege("01091443409",sendRandNumInter.getRandNum()); 
    }
    @Transactional(rollbackFor = Exception.class)
    public void sendPhone(trySendSmsDto sendSmsDto) {
        System.out.println("sendPhone");
        System.out.println(sendSmsDto.getUnit());
        phoneVo phoneVo=null;
        if(sendSmsDto.getDetail().equals("confrim")){
            System.out.println("회원가입시 핸드폰 번호 요청");
            phoneVo=phoneDao.findByPhoneNum(sendSmsDto.getUnit()).orElseGet(() -> new phoneVo().builder().pcount(0).pcreated(Timestamp.valueOf(LocalDateTime.now())).phoneNum(sendSmsDto.getUnit()).build());
        }else{
            System.out.println("이미 회원가입 되어있는 휴대폰 번호 찾기");
        }
        System.out.println("문자메세지 전송"+phoneVo);
        sendRandNumInter sendRandNumInter=new sendInter(phoneVo.getPcount(),phoneVo.getPhoneNum(),phoneVo.getPcreated(),utillService.getRandomNum(numLength),phoneVo.getDonePhone(),sendSmsDto.getScope());
        sendRandNum(sendRandNumInter);
        //sendMessage.sendMessege("01091443409",sendRandNumInter.getRandNum()); 
    }
    private void sendRandNum(sendRandNumInter sendRandNumInter){
        System.out.println("sendRandNum");
        int reuqestCount=sendRandNumInter.getCount();
        LocalDateTime firstRequest=sendRandNumInter.getCreated().toLocalDateTime();
        System.out.println(reuqestCount+1+"번째 요청시도");
        try {
            if(reuqestCount==0){
                System.out.println("첫 인증번호 요청");
            }else if(firstRequest.isBefore(firstRequest.plusDays(limitDay))){
                System.out.println("첫 요청후 하루가 지나지않음");
                if(reuqestCount<=maxReuqest){
                    System.out.println(maxReuqest+"회 이하입니다");
                    update(sendRandNumInter);
                    System.out.println("요청 db 수정완료");
                }else{
                    System.out.println("첫 요청 후 하루가 지나지않고 최대 회수 초과한 상태");
                    utillService.throwRuntimeEX(new RuntimeException(),"하루 최대 요청 횟수는 "+maxReuqest+"입니다 24시간뒤에 시도해주세요","sendRandNum");
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
        if(sendRandNumInter.getScope().equals("phone")){
            System.out.println("전화인증 db 저장");
            phoneDao.save((phoneVo)interToPhoneVo(sendRandNumInter).get("vo"));
        }else if(sendRandNumInter.getScope().equals("email")){
            System.out.println("이메일 인증 db저장");
            emailDao.save((emailVo)interToPhoneVo(sendRandNumInter).get("vo"));
        }
    }
    private void delete(sendRandNumInter sendRandNumInter){
        System.out.println("delete");
        if(sendRandNumInter.getScope().equals("phone")){
            System.out.println("핸드폰 인증 정보 삭제");
            phoneDao.deleteByPhoneNum(sendRandNumInter.getEmailOrPhone());
            return;
        }
    }
    private void update(sendRandNumInter sendRandNumInter){
        System.out.println("update");
        if(sendRandNumInter.getScope().equals("phone")){
            System.out.println("핸드폰 요청 횟수 증가");
            phoneDao.updatePhoneNative(sendRandNumInter.getCount()+1,sendRandNumInter.getRandNum(),sendRandNumInter.getEmailOrPhone());
        }else if(sendRandNumInter.getScope().equals("email")){
            System.out.println("이메일 요청 횟수 증가");
            emailDao.updateEmailNative(sendRandNumInter.getCount()+1, sendRandNumInter.getRandNum(), sendRandNumInter.getEmailOrPhone());
        }
    }
    private Map<String,Object> interToPhoneVo(sendRandNumInter sendRandNumInter){
        System.out.println("interToVo");
        Map<String,Object>map=new HashMap<>();
        if(sendRandNumInter.getScope().equals("phone")){
            System.out.println("phonevo로 변환");
            phoneVo vo=phoneVo.builder()
            .pcount(1)
            .phoneNum(sendRandNumInter.getEmailOrPhone())
            .randNum(sendRandNumInter.getRandNum())
            .donePhone(noDoneNum)
            .build();
            map.put("vo", vo);
        }else if(sendRandNumInter.getScope().equals("email")){
            System.out.println("emailco로 변환");
            emailVo vo=emailVo.builder()
            .ecount(1)
            .eemail(sendRandNumInter.getEmailOrPhone())
            .erandNum(sendRandNumInter.getRandNum())
            .doneemail(noDoneNum)
            .build();
            map.put("vo", vo);
        }
  
        return map;
    }
    @Transactional
    public JSONObject checkRandNum(tryConfrimRandNumDto tryConfrimRandNumDto) throws IllegalArgumentException {
        System.out.println("checkRandNum");
        try {
            if(tryConfrimRandNumDto.getUnit().equals("phone")){
                phoneVo phoneVo=phoneDao.findByPhoneNum(tryConfrimRandNumDto.getPhone()).orElseThrow(()->new IllegalArgumentException("인증 요청 내역이 존재 하지 않습니다"));
                confrimNum(tryConfrimRandNumDto.getRandNum(), phoneVo.getRandNum());
                phoneVo.setDonePhone(doneNum);
            }
            return utillService.makeJson(true, "인증이 완료되었습니다");
        }catch (RuntimeException e) {
            return utillService.makeJson(false,  e.getMessage());
        }catch (Exception e) {
            return utillService.makeJson(false, "인증번호 검증 오류");
        }
    }
    private void confrimNum(String submitNum,String dbNum) {
        System.out.println("confrimNuM");
        if(submitNum.equals(dbNum.trim())){
            System.out.println("인증번호 일치");
            return;
        }
        System.out.println("인증번호 불일치");
        throw new RuntimeException("인증번호 불일치");
    }
    public void delete(String phone) {
        System.out.println("delete"+phone);
        phoneDao.deleteByPhoneNum(phone);
    }
    
}
