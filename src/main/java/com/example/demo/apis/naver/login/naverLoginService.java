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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class naverLoginService {
    private final static Logger LOGGER=LoggerFactory.getLogger(naverLoginService.class);
    @Value("${naver.loginCallback}")
    private String loginCallback;
    @Autowired
    private userService userService;
    @Autowired
    private requestTo requestTo;
    
    public JSONObject getNaverLogin(String naverId) {
        LOGGER.info("getNaverLogin");
        String state="";
        try {
            state = URLEncoder.encode(loginCallback, "UTF-8");
            return utillService.makeJson(true, "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+naverId+"&redirect_uri="+""+loginCallback+""+"&state="+state+"");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException("naverLogin 오류 발생");
        } 
    }
    public uservo tryNaverLogin(JSONObject tokens,HttpServletResponse response) {
        LOGGER.info("tryNaverLogin naverLoginService");
        HttpHeaders headers=requestTo.getHeaders();
        headers.add("Authorization","Bearer " + tokens.get("access_token"));
        JSONObject getNaver=requestTo.requestToApi("https://openapi.naver.com/v1/nid/me", headers); 
        LOGGER.info(getNaver.toString());
        return userService.insertOauth(makeVo((LinkedHashMap<String,Object>)getNaver.get("response")));
    }
    private uservo makeVo(LinkedHashMap<String,Object> getNaver) {
        LOGGER.info("makeVo");
        uservo vo=uservo.builder()
                        .email((String)getNaver.get("email"))
                        .name((String)getNaver.get("name"))
                        .phoneNum(getNaver.get("mobile").toString().replace("-", ""))
                        .address("테스트계정 주소 안줌")
                        .provider("naver")
                        .role(Stringenums.role_user.getString())
                        .build();
        LOGGER.info("통과");                
        return vo;
    }


}
