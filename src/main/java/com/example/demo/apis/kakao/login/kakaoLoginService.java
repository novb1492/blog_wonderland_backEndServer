package com.example.demo.apis.kakao.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.apis.requestTo;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class kakaoLoginService {

    @Value("${kakao.login.callback}")
    private String loginCallbackUrl;
    
    @Autowired
    private requestTo requestTo;
   

    public String showLoingPage(String apikey) {
        System.out.println("kakaoLoginService showLoginPage");
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+loginCallbackUrl+"";
    }
    public void tryLogin(HttpServletRequest request,HttpServletResponse response,String apiKey) {
        System.out.println("tryLogin");
        System.out.println(getKakaoToken(request.getParameter("code").toString(), apiKey));
    }
    private JSONObject getKakaoToken(String code,String apikey) {
        System.out.println("getKakaoToken");
        HttpHeaders headers=new HttpHeaders();
        MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        body.add("grant_type", "authorization_code");
        body.add("client_id", apikey);
        body.add("redirect_uri", loginCallbackUrl);
        body.add("code", code);
        return requestTo.requestToKakao(body, "https://kauth.kakao.com/oauth/token", headers);
    }
}
