package com.example.demo.find.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.example.demo.apis.mailSender.sendMailService;
import com.example.demo.find.model.findPwdDao;
import com.example.demo.find.model.findPwdVo;
import com.example.demo.find.model.getJoinUsers;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.utill.utillService;

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
            sendMailService.sendEmail(email,"안녕하세요 wonderland입니다","비밀번호 변경링크입니다 "+frontDamain+"/findPwdPage?object="+token);
        }catch (RuntimeException e) {
            utillService.throwRuntimeEX(e, e.getMessage() ,"findPwd");
        }catch (Exception e) {
            utillService.throwRuntimeEX(e,"알수 없는 오류 발생", "findPwd");
        }
      
    }
    private void confrim(int ucount) {
        System.out.println("confrim");
        if(ucount==0){
            System.out.println("회원가입된 사용자가 아닙니다");
            throw new RuntimeException("회원가입된 사용자가 아닙니다");
        }
        System.out.println("비밀번호 변경/아이디찾기 요청 유효성 통과");
    }
}
