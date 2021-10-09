package com.example.demo.user.service;



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

@Service
public class userService {
    private final String data=Stringenums.data.getString();

    @Autowired
    private userDao userDao;

    public JSONObject checkLogin() {
        System.out.println("checkLogin");
        JSONObject response=utillService.makeJson(true,"로그인 사용자입니다" );
        response.put(data, sendUserInfor());
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
    public JSONObject insert(tryJoinDto tryJoinDto) {
        System.out.println("insert");
        try {
            confrim(tryJoinDto);
            uservo vo=uservo.builder()
                            .email(tryJoinDto.getEmail())
                            .name(tryJoinDto.getName())
                            .phoneNum(tryJoinDto.getPhone())
                            .pwd(tryJoinDto.getPwd())
                            .address(tryJoinDto.getPostcode()+","+tryJoinDto.getAddress()+","+tryJoinDto.getDetailAddess())
                            .role(Stringenums.role_user.getString())
                            .build();
                            userDao.save(vo);
                            return utillService.makeJson(true, "회원가입을 축하드립니다");
        }catch (RuntimeException e) {
            utillService.throwRuntimeEX(e, e.getMessage(), "insert");
        } catch (Exception e) {
            utillService.throwRuntimeEX(e,"회원가입에 실패했습니다", "insert");
        }
        return null;
    }
    private void confrim(tryJoinDto tryJoinDto) {
        System.out.println("checkPhoneConfrim");
            inserConfrimInter inserConfrimInter=userDao.findByEmailJoinConfrim(tryJoinDto.getEmail(), tryJoinDto.getPhone());
            int done=inserConfrimInter.getDone().orElseThrow(()->new IllegalArgumentException("요청내역이 존재하지 않습니다"));
            int count=inserConfrimInter.getUcount();
            String message=null;
            if(done==0){
                System.out.println("인증되지 않은 핸든폰입니다");
                message="인증되지 않은 핸든폰입니다";
            }else if(count!=0){
                System.out.println("이미 존재하는 핸드폰 입니다");
                message="이미 존재하는 핸드폰 입니다";
            }else if(!tryJoinDto.getPwd().equals(tryJoinDto.getPwd2())){
                System.out.println("비밀번호가 일치하지 않습니다");
                message="비밀번호가 일치하지 않습니다";
            }else{
                System.out.println("회원가입 유효성 통과");
                return;
            }
            System.out.println("rtun");
            throw new RuntimeException(message);
      
            
      
        
    }
}
