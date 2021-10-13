package com.example.demo.user.service;




import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.config.securityConfig;

import com.example.demo.confrim.service.confrimService;
import com.example.demo.enums.Stringenums;
import com.example.demo.enums.intEnums;
import com.example.demo.find.model.findPwdDao;

import com.example.demo.find.model.getJoinRequest;


import com.example.demo.user.model.inserConfrimInter;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.tryJoinDto;
import com.example.demo.user.model.tryUpadateDto;
import com.example.demo.user.model.userDao;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.dom4j.IllegalAddException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class userService {
    private final String data=Stringenums.data.getString();
    private final int doneNum=intEnums.doneNum.getInt();

    @Autowired
    private userDao userDao;
    @Autowired
    private securityConfig securityConfig;
    @Autowired
    private confrimService confrimService;
    @Autowired
    private findPwdDao findPwdDao;


    
    @Value("${oauth.pwd}")
    private String oauthPwd;

    public JSONObject checkLogin(HttpServletRequest request) {
        System.out.println("checkLogin");
        JSONObject response=utillService.makeJson(true,"로그인 사용자입니다" );
        uservo uservo=sendUserInfor();
        System.out.println(request.getRequestURI());
        if(Optional.ofNullable(request.getParameter("scope")).orElseGet(()->"emthy").equals("all")){
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
            uservo.setPwd(securityConfig.pwdEncoder().encode(oauthPwd));
            userDao.save(uservo);
            dbVo=uservo;
        }
        return dbVo;
    }
    public JSONObject update(tryUpadateDto tryUpadateDto) {
        System.out.println("update");
        if(tryUpadateDto.getScope().equals("pwd")&&tryUpadateDto.getDetail().equals("find")){
            System.out.println("비밀번호 변경 요청");
            updatePwd(tryUpadateDto);
            findPwdDao.deleteJoinRequest(tryUpadateDto.getToken());
        }else if(tryUpadateDto.getScope().equals("address")){
            System.out.println("주소변경 요청");
            updateAddress(tryUpadateDto);
        }else if(tryUpadateDto.getScope().equals("phone")){

        }else{
            return utillService.makeJson(false, "변경사항이 유효하지 않습니다");
        }
        return utillService.makeJson(true, "변경에 성공했습니다");
    }
    private void updateAddress(tryUpadateDto tryUpadateDto) {
        System.out.println("updateAddress");
        String postcode=Optional.ofNullable(tryUpadateDto.getPostcode()).orElseThrow(()->new IllegalAddException("우편번호가 빈칸입니다"));
        String address=Optional.ofNullable(tryUpadateDto.getAddress()).orElseThrow(()->new IllegalAddException("주소가 빈칸입니다"));
        String detailAddress=Optional.ofNullable(tryUpadateDto.getDetailAddress()).orElseThrow(()->new IllegalAddException("상세주소가 빈칸입니다"));
        confrim(postcode, address, detailAddress);
        String email=sendUserInfor().getEmail();
        String fullAddress=postcode+","+address+","+detailAddress;
        userDao.updateAddress(fullAddress, email);
    }
    private void confrim(String postcode,String address,String detailAddress) {
        System.out.println("confrim");
        if(postcode.isBlank()||address.isBlank()||detailAddress.isBlank()){
            throw new RuntimeException("주소에 빈칸이 존재합니다");
        }
        System.out.println("주소유효성 통과");
    }
    private void updatePwd(tryUpadateDto tryUpadateDto) {
        System.out.println("updatePwd");
        try {
            confrimPwd(tryUpadateDto);
            String email=null;
            if(tryUpadateDto.getDetail().equals("find")){
                getJoinRequest getJoinRequest=findPwdDao.findTokenNameJoinRequest(tryUpadateDto.getToken());
                confrim(getJoinRequest);
                email=getJoinRequest.getEmail();
            }else if(tryUpadateDto.getDetail().equals("update")){
                email=sendUserInfor().getEmail();
            }else{
                throw new RuntimeException("디테일값이 유효하지 않습니다");
            }
            
            userDao.updatePwd(securityConfig.pwdEncoder().encode(tryUpadateDto.getPwd()),email);
        }catch (RuntimeException e) {
            utillService.throwRuntimeEX(e, e.getMessage(), "updatePwd");
        }catch (Exception e) {
            utillService.throwRuntimeEX(e, "알수 없는 오류가 발생했습니다", "updatePwd");
        }
    }
    private void confrim(getJoinRequest getJoinRequest) {
        System.out.println("confrimDate");
        String message=null;
        if(getJoinRequest.getEmail()==null){
            message="존재하지 않는 회원입니다";
        }else if(LocalDateTime.now().isAfter(getJoinRequest.getPexpire().toLocalDateTime())){
            message="만료되었습니다 다시 요청바랍니다";
        }else if(!getJoinRequest.getDoneEmail().equals(Integer.toString(doneNum))){
            message="인증이 완료되지 않았습니다";
        }else{
            System.out.println("유효성검사 통과");
            return;
        }
        throw new RuntimeException(message);
    }
    private void confrimPwd(tryUpadateDto tryUpadateDto) {
        System.out.println("confrimPwd");
        String pwd=tryUpadateDto.getPwd();
        String pwd2=tryUpadateDto.getPwd2();
        int pwdminLength=intEnums.pwdMin.getInt(); 
        int pwdmaxLength=intEnums.pwdMin.getInt();

        if(utillService.checkBlankOrNull(pwd)||utillService.checkBlankOrNull(pwd2)){
            System.out.println("비밀번호 빈칸 발견");
            throw new RuntimeException("비밀번호 빈칸 발견");
        }else if(!utillService.checkEquals(pwd, pwd2)){
            System.out.println("비밀번호 불일치");
            throw new RuntimeException("비밀번호 불일치");
        }
        String lengthResult=utillService.checkLength(pwdminLength, pwdmaxLength, pwd);
        if(lengthResult.equals(Stringenums.tooSmall.getString())||lengthResult.equals(Stringenums.tooBig.getString())){
            throw new RuntimeException("비밀번호는 최소 4자리 최대10자리입니다");
        }   
    }
}
