package com.example.demo.user.service;



import javax.servlet.http.HttpServletRequest;

import com.example.demo.config.securityConfig;
import com.example.demo.confrim.service.confrimService;
import com.example.demo.enums.Stringenums;
import com.example.demo.user.model.inserConfrimInter;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.tryJoinDto;
import com.example.demo.user.model.userDao;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class userService {
    private final String data=Stringenums.data.getString();

    @Autowired
    private userDao userDao;
    @Autowired
    private securityConfig securityConfig;
    @Autowired
    private confrimService confrimService;

    public JSONObject checkLogin(HttpServletRequest request) {
        System.out.println("checkLogin");
        JSONObject response=utillService.makeJson(true,"로그인 사용자입니다" );
        uservo uservo=sendUserInfor();
        if(request.getRequestURI().equals("/user/checkLogin&scope=all")){
            response.put(data, uservo);
        }
        return response;
        
    }
    public uservo sendUserInfor() {
        String methodName="sendUserInfor";
        System.out.println(methodName);
        try {
            principalDetail principalDetail=(principalDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            uservo uservo=principalDetail.getUservo();
            uservo.setPwd(null);
            return uservo;
        } catch (ClassCastException e) {
            utillService.throwRuntimeEX(e,"비로그인 사용자입니다", methodName);
          
        }catch (Exception e){
            utillService.throwRuntimeEX(e,"로그인 정보 조회에 실패했습니다", methodName);
        }
        return null;
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject insert(tryJoinDto tryJoinDto) {
        System.out.println("insert");
        try {
            confrim(tryJoinDto);
            uservo vo=uservo.builder()
                            .email(tryJoinDto.getEmail())
                            .name(tryJoinDto.getName())
                            .phoneNum(tryJoinDto.getPhone())
                            .pwd(securityConfig.pwdEncoder().encode(tryJoinDto.getPwd()))
                            .address(tryJoinDto.getPostcode()+","+tryJoinDto.getAddress()+","+tryJoinDto.getDetailAddess())
                            .role(Stringenums.role_user.getString())
                            .build();
                            userDao.save(vo);
                            confrimService.delete(tryJoinDto.getPhone());
                            return utillService.makeJson(true, "회원가입을 축하드립니다");
        }catch (RuntimeException e) {
            return utillService.makeJson(false, e.getMessage());
        } catch (Exception e) {
            return utillService.makeJson(false, e.getMessage());
        }
    }
    private void confrim(tryJoinDto tryJoinDto) {
        System.out.println("checkPhoneConfrim");
            inserConfrimInter inserConfrimInter=userDao.findByEmailJoinConfrim(tryJoinDto.getPhone(),tryJoinDto.getEmail(), tryJoinDto.getPhone());
            int done=inserConfrimInter.getDone().orElseThrow(()->new IllegalArgumentException("요청내역이 존재하지 않습니다"));
            String message=null;
            if(done==0){
                System.out.println("인증되지 않은 핸든폰입니다");
                message="인증되지 않은 핸든폰입니다";
            }else if(inserConfrimInter.getUcount()!=0){
                System.out.println("이미 존재하는 이메일 입니다");
                message="이미 존재하는 이메일 입니다";
            }else if(!tryJoinDto.getPwd().equals(tryJoinDto.getPwd2())){
                System.out.println("비밀번호가 일치하지 않습니다");
                message="비밀번호가 일치하지 않습니다";
            }else if(inserConfrimInter.getPcount()>0){
                System.out.println("이미 존재하는 핸드폰번호 입니다");
                message="이미 존재하는 핸드폰번호 입니다";
            }else{
                System.out.println("회원가입 유효성 통과");
                return;
            }
            throw new RuntimeException(message);  
    }
    public JSONObject checkSucLogin() {
        System.out.println("checkSucLogin");
        try {
            principalDetail principalDetail=(principalDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println("로그인 성공:"+principalDetail.getUservo().getEmail());
            return utillService.makeJson(true, "로그인 성공");
        } catch (NullPointerException e) {
            return utillService.makeJson(false,"아이디 혹은 비밀번호가 일치 하지않습니다");
        }
    }
    public uservo insertOauth(uservo uservo) {
        System.out.println("insertOauth");
        uservo dbVo=userDao.findByEmail(uservo.getEmail()).orElseGet(()-> new uservo());
        if(dbVo.getUid()==0){
            System.out.println(uservo.getProvider()+"로그인 회원가입시도");
            uservo.setPwd(securityConfig.pwdEncoder().encode(uservo.getPwd()));
            userDao.save(uservo);
            dbVo=uservo;
        }
        return dbVo;
    }
}
