package com.example.demo.apis.naver.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.apis.requestTo;
import com.example.demo.enums.Stringenums;
import com.example.demo.user.model.uservo;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class naverLoginService {

    @Value("${naver.loginCallback}")
    private String loginCallback;
    @Autowired
    private userService userService;
    @Autowired
    private requestTo requestTo;
    
    public JSONObject getNaverLogin(String naverId) {
        String state="";
        try {
            state = URLEncoder.encode(loginCallback, "UTF-8");
            return utillService.makeJson(true, "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+naverId+"&redirect_uri="+""+loginCallback+""+"&state="+state+"");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException("naverLogin 오류 발생");
        } 
    }
    public uservo tryNaverLogin(JSONObject tokens,HttpServletResponse response) {
        System.out.println("tryNaverLogin naverLoginService");
        HttpHeaders headers=new HttpHeaders();
        headers.add("Authorization","Bearer " + tokens.get("access_token"));
        JSONObject getNaver=requestTo.requestToKakao("https://openapi.naver.com/v1/nid/me", headers); //requestToNaver(tokens.get("access_token").toString(),"https://openapi.naver.com/v1/nid/me");
        System.out.println(getNaver);
        return userService.insertOauth(makeVo((LinkedHashMap<String,Object>)getNaver.get("response")));
    }
    private uservo makeVo(LinkedHashMap<String,Object> getNaver) {
        System.out.println("makeVo");
        uservo vo=uservo.builder()
                        .email((String)getNaver.get("email"))
                        .name((String)getNaver.get("name"))
                        .phoneNum(getNaver.get("mobile").toString().replace("-", ""))
                        .address("테스트계정 주소 안줌")
                        .provider("naver")
                        .pwd("oauthPwd")
                        .role(Stringenums.role_user.getString())
                        .build();
        System.out.println("통과");                
        return vo;
    }


}
