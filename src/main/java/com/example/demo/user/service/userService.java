package com.example.demo.user.service;




import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.config.securityConfig;
import com.example.demo.enums.Stringenums;
import com.example.demo.enums.intEnums;
import com.example.demo.find.model.findPwdDao;
import com.example.demo.find.model.getJoinRequest;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.user.model.inserConfrimInter;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.tryJoinDto;
import com.example.demo.user.model.tryUpadateDto;
import com.example.demo.user.model.userDao;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.dom4j.IllegalAddException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class userService {
    private final static Logger LOGGER=LoggerFactory.getLogger(userService.class);
    private final String data=Stringenums.data.getString();
    private final int doneNum=intEnums.doneNum.getInt();
    private final String confrim=Stringenums.confrim.getString();
    private final String find=Stringenums.find.getString();

    @Autowired
    private userDao userDao;
    @Autowired
    private securityConfig securityConfig;
    @Autowired
    private findPwdDao findPwdDao;
    @Autowired
    private jwtService jwtService;


    @Value("${jwt.access.name}")
    private String accessTokenName;
    @Value("${jwt.refresh.name}")
    private String refreshTokenName;
    @Value("${oauth.pwd}")
    private String oauthPwd;
    

    public JSONObject checkLogin(HttpServletRequest request) {
        LOGGER.info("checkLogin");
        JSONObject response=utillService.makeJson(true,"로그인 사용자입니다" );
        uservo uservo=sendUserInfor();
        LOGGER.info(request.getRequestURI());
        if(Optional.ofNullable(request.getParameter("scope")).orElseGet(()->"emthy").equals("all")){
            response.put(data, uservo);
        }else{
            response.put("email", uservo.getEmail());
        }
        return response;
        
    }
    public uservo sendUserInfor() {
        String methodName="sendUserInfor";
        LOGGER.info(methodName);
        String errorMessag=Stringenums.defalutErrorMessage.getString();
        try {
            principalDetail principalDetail=(principalDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            uservo uservo=principalDetail.getUservo();
            uservo.setPwd(null);
            return uservo;
        } catch (ClassCastException e) {
            errorMessag="비로그인 사용자입니다";
        }catch (Exception e){
            errorMessag="로그인 정보 조회에 실패했습니다";
        }
        throw utillService.makeRuntimeEX(errorMessag, methodName);
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject insert(tryJoinDto tryJoinDto) {
        LOGGER.info("insert");
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
                            userDao.deletePhone(tryJoinDto.getPhone(), confrim);
                            return utillService.makeJson(true, "회원가입을 축하드립니다");
        }catch (RuntimeException e) {
            return utillService.makeJson(false, e.getMessage());
        } catch (Exception e) {
            return utillService.makeJson(false, e.getMessage());
        }
    }
    private void confrim(tryJoinDto tryJoinDto) {
        LOGGER.info("checkPhoneConfrim");
            String phone=tryJoinDto.getPhone();
            inserConfrimInter inserConfrimInter=userDao.findByEmailJoinConfrim(phone,tryJoinDto.getEmail(), phone,confrim);
            int done=inserConfrimInter.getDone().orElseThrow(()->new IllegalArgumentException("요청내역이 존재하지 않습니다"));
            String message=null;
            if(done==0){
                LOGGER.info("인증되지 않은 핸든폰입니다");
                message="인증되지 않은 핸든폰입니다";
                userDao.deletePhone(phone, confrim);
            }else if(inserConfrimInter.getUcount()!=0){
                LOGGER.info("이미 존재하는 이메일 입니다");
                message="이미 존재하는 이메일 입니다";
            }else if(!tryJoinDto.getPwd().equals(tryJoinDto.getPwd2())){
                LOGGER.info("비밀번호가 일치하지 않습니다");
                message="비밀번호가 일치하지 않습니다";
            }else if(inserConfrimInter.getPcount()>0){
                LOGGER.info("이미 존재하는 핸드폰번호 입니다");
                message="이미 존재하는 핸드폰번호 입니다";
                userDao.deletePhone(phone, confrim);
            }else{
                LOGGER.info("회원가입 유효성 통과");
                return;
            }
            throw new RuntimeException(message);  
    }
    public JSONObject checkSucLogin() {
        LOGGER.info("checkSucLogin");
        try {
            principalDetail principalDetail=(principalDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            LOGGER.info("로그인 성공:"+principalDetail.getUservo().getEmail());
            return utillService.makeJson(true, "로그인 성공");
        } catch (NullPointerException e) {
            return utillService.makeJson(false,"아이디 혹은 비밀번호가 일치 하지않습니다");
        }
    }
    public uservo insertOauth(uservo uservo) {
        LOGGER.info("insertOauth");
        uservo dbVo=userDao.findByEmail(uservo.getEmail()).orElseGet(()-> new uservo());
        if(dbVo.getUid()==0){
            LOGGER.info(uservo+"로그인 회원가입시도");
            uservo.setPwd(securityConfig.pwdEncoder().encode(oauthPwd));
            userDao.save(uservo);
            dbVo=uservo;
        }
        return dbVo;
    }
    public JSONObject update(tryUpadateDto tryUpadateDto) {
        LOGGER.info("update");
        if(tryUpadateDto.getScope().equals("pwd")){
            LOGGER.info("비밀번호 변경 요청");
            updatePwd(tryUpadateDto);
            findPwdDao.deleteJoinRequest(tryUpadateDto.getToken());
        }else if(tryUpadateDto.getScope().equals("address")){
            LOGGER.info("주소변경 요청");
            updateAddress(tryUpadateDto);
        }else if(tryUpadateDto.getScope().equals("phone")){
            LOGGER.info("휴대폰 번호 변경");
            updatePhone(tryUpadateDto.getPhone());
        }else{
            return utillService.makeJson(false, "변경사항이 유효하지 않습니다");
        }
        return utillService.makeJson(true, "변경에 성공했습니다");
    }
    private void updatePhone(String phone) {
        LOGGER.info("updatePhone");
        uservo uservo=sendUserInfor();
        userDao.updatePhone(phone, uservo.getEmail());
    }
    private void updateAddress(tryUpadateDto tryUpadateDto) {
        LOGGER.info("updateAddress");
        String postcode=Optional.ofNullable(tryUpadateDto.getPostcode()).orElseThrow(()->new IllegalAddException("우편번호가 빈칸입니다"));
        String address=Optional.ofNullable(tryUpadateDto.getAddress()).orElseThrow(()->new IllegalAddException("주소가 빈칸입니다"));
        String detailAddress=Optional.ofNullable(tryUpadateDto.getDetailAddress()).orElseThrow(()->new IllegalAddException("상세주소가 빈칸입니다"));
        confrim(postcode, address, detailAddress);
        String email=sendUserInfor().getEmail();
        String fullAddress=postcode+","+address+","+detailAddress;
        userDao.updateAddress(fullAddress, email);
    }
    private void confrim(String postcode,String address,String detailAddress) {
        LOGGER.info("confrim");
        if(utillService.checkBlankOrNull(postcode)||utillService.checkBlankOrNull(address)||utillService.checkBlankOrNull(detailAddress)){
            throw new RuntimeException("주소에 빈칸이 존재합니다");
        }
        LOGGER.info("주소유효성 통과");
    }
    private void updatePwd(tryUpadateDto tryUpadateDto) {
        LOGGER.info("updatePwd");
        String errorMessage="알수 없는 오류 발생";
        try {
            confrimPwd(tryUpadateDto);
            String email=null;
            if(tryUpadateDto.getDetail().equals(find)){
                getJoinRequest getJoinRequest=findPwdDao.findTokenNameJoinRequest(find,tryUpadateDto.getToken());
                confrim(getJoinRequest);
                email=getJoinRequest.getEmail();
            }else if(tryUpadateDto.getDetail().equals("update")){
                email=sendUserInfor().getEmail();
                uservo uservo=userDao.findByEmail(email).orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자입니다"));
                LOGGER.info(tryUpadateDto.getOriginPwd()+"기존");
                if(!securityConfig.pwdEncoder().matches(tryUpadateDto.getOriginPwd(),uservo.getPwd())){
                    throw new RuntimeException("기존비밀번호가 불일치 합니다");
                }
            }else{
                throw new RuntimeException("디테일값이 유효하지 않습니다");
            }
            userDao.updatePwd(securityConfig.pwdEncoder().encode(tryUpadateDto.getPwd()),email);
            return;
        }catch (RuntimeException e) {
            errorMessage=e.getMessage();
        }
        throw utillService.makeRuntimeEX(errorMessage, "updatePwd");
    }
    private void confrim(getJoinRequest getJoinRequest) {
        LOGGER.info("confrimDate");
        String message=null;
        if(utillService.checkBlankOrNull(getJoinRequest.getEmail())){
            message="존재하지 않는 회원입니다";
        }else if(LocalDateTime.now().isAfter(getJoinRequest.getPexpire().toLocalDateTime())){
            message="만료되었습니다 다시 요청바랍니다";
        }else if(!utillService.checkEquals(Integer.toString(doneNum), getJoinRequest.getDoneEmail())){
            message="인증이 완료되지 않았습니다";
        }else{
            LOGGER.info("유효성검사 통과");
            return;
        }
        throw new RuntimeException(message);
    }
    private void confrimPwd(tryUpadateDto tryUpadateDto) {
        LOGGER.info("confrimPwd");
        String pwd=tryUpadateDto.getPwd();
        String pwd2=tryUpadateDto.getPwd2();
        int pwdminLength=intEnums.pwdMin.getInt(); 
        int pwdmaxLength=intEnums.pwdMin.getInt();

        if(utillService.checkBlankOrNull(pwd)||utillService.checkBlankOrNull(pwd2)){
            LOGGER.info("비밀번호 빈칸 발견");
            throw new RuntimeException("비밀번호 빈칸 발견");
        }else if(!utillService.checkEquals(pwd, pwd2)){
            LOGGER.info("비밀번호 불일치");
            throw new RuntimeException("비밀번호 불일치");
        }
        String lengthResult=utillService.checkLength(pwdminLength, pwdmaxLength, pwd);
        if(lengthResult.equals(Stringenums.tooSmall.getString())||lengthResult.equals(Stringenums.tooBig.getString())){
            throw new RuntimeException("비밀번호는 최소 4자리 최대10자리입니다");
        }   
        if(tryUpadateDto.getDetail().equals(Stringenums.update.getString())){
            LOGGER.info("마이페이지 비밀번호 변경시도");
            String originPwd=tryUpadateDto.getOriginPwd();
            if(utillService.checkBlankOrNull(originPwd)){
                throw new RuntimeException("기존 비밀번호를 입력해주세요");
            }else if(!utillService.checkLength(4, 10, originPwd).equals(Stringenums.collect.getString())){
                throw new RuntimeException("비밀번호는 최소 4자리 최대10자리입니다");
            }
        }
        LOGGER.info(" 비밀번효 유효성 통과");
    }
    public JSONObject logOut(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("logOut");
        List<String>tokens=new ArrayList<>();
        tokens.add(accessTokenName);
        tokens.add(refreshTokenName);
        for(String s:tokens){
            utillService.deleteCookie(s, request, response);
        }
        jwtService.delete(refreshTokenName);
        return utillService.makeJson(true, "로그아웃");
    }
}
