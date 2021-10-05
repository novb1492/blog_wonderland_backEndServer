package com.example.demo.user.service;



import com.example.demo.enums.Stringenums;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class userService {
    private final String data=Stringenums.data.getString();

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
}
