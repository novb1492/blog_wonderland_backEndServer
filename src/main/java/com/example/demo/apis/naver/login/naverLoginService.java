package com.example.demo.apis.naver.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.apis.naver.naverService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class naverLoginService {

    @Value("${naver.loginCallback}")
    private String loginCallback;
    @Autowired
    private naverService naverService;
    
    public JSONObject getNaverLogin(String naverId) {
        String state="";
        try {
            state = URLEncoder.encode(loginCallback, "UTF-8");
            return utillService.makeJson(true, "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+naverId+"&redirect_uri="+""+loginCallback+""+"&state="+state+"");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException("naverLogin 오류 발생");
        } 
    }
    public void tryNaverLogin(JSONObject tokens,HttpServletResponse response) {
        System.out.println("tryNaverLogin naverLoginService");
        System.out.println(tokens);
        JSONObject getNaver=naverService.requestToNaver((String)tokens.get("access_token"),"https://openapi.naver.com/v1/nid/me");
        System.out.println(getNaver);
    }


}
