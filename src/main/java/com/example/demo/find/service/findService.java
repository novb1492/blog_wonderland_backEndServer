package com.example.demo.find.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.example.demo.apis.coolsms.sendMessage;
import com.example.demo.apis.mailSender.sendMailService;
import com.example.demo.enums.Stringenums;
import com.example.demo.find.model.findPwdDao;
import com.example.demo.find.model.findPwdVo;
import com.example.demo.find.model.getJoinUsers;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class findService {
  
    @Autowired
    private findPwdDao findPwdDao;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private sendMailService sendMailService;
    @Value("${front.domain}")
    private String frontDamain;
    @Value("${find.Pwd.Expire}")
    private int period;


    public void findPwd(String email) {
        System.out.println("findPwd");
        String errorMessage=Stringenums.defalutErrorMessage.getString();
        try {
            getJoinUsers getJoinUsers=findPwdDao.findJoinUsers(email,email);
            confrim(getJoinUsers.getUcount());
            String token=jwtService.getRefreshToken();
            findPwdVo findPwdVo=new findPwdVo();
            if(getJoinUsers.getPcount()!=0){
                System.out.println("기존 비밀번요 변경페이이 요청존재");
                LocalDateTime now=LocalDateTime.now();
                findPwdDao.updateTokenNative(Timestamp.valueOf(now), Timestamp.valueOf(now.plusDays(period)),token, email);
            }else{
                System.out.println("첫요청");
                findPwdVo.setPemail(email);
                findPwdVo.setPexpire(Timestamp.valueOf(LocalDateTime.now().plusDays(period)));
                findPwdVo.setPtokenName(token);
                findPwdDao.save(findPwdVo);
            }
            sendMailService.sendEmail(email,"안녕하세요 wonderland입니다","비밀번호 변경링크입니다 "+frontDamain+"changePwdPage?scope=pwd&object="+token);
        }catch (RuntimeException e) {
            errorMessage=e.getMessage();
        }
      throw  utillService.makeRuntimeEX(errorMessage, "findPwd");
    }
    private void confrim(int ucount) {
        System.out.println("confrim");
        if(ucount==0){
            System.out.println("회원가입된 사용자가 아닙니다");
            throw new RuntimeException("회원가입된 사용자가 아닙니다");
        }
        System.out.println("비밀번호 변경/아이디찾기 요청 유효성 통과");
    }
    public JSONObject findRequest(String token,String scope) {
        System.out.println("findRequest");
        int count=0;
        if(scope.equals("pwd")){
            System.out.println("비밀번호 찾기 내역 확인");
            count=findPwdDao.countByPtokenNameNative(token);
        }else{
            System.out.println("이메일 찾기 내역 확인");
        }
        System.out.println(count+"카운트");
        if(count==0){
           return utillService.makeJson(false, "변경요청 내역 없음");
        }
        return utillService.makeJson(true, "변경요청 내역 존재");
    }
    public void findEmail(String phone) {
        System.out.println("findEmail");
        String email=findPwdDao.findEmailNative(phone).orElseThrow(()->new IllegalArgumentException("회원가입된 핸드폰이 아닙니다"));
        sendMessage.sendMessege(phone, "안녕하세요 wonderland입니다 이메일은 "+email+"입니다");
        findPwdDao.deleteRequest(phone);
    }
}
