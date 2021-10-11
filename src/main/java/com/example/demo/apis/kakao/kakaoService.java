package com.example.demo.apis.kakao;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.apis.kakao.login.kakaoLoginService;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class kakaoService {

    
    @Value("${kakao.app.key}")
    private String appKey;
    @Value("${kakao.rest.key}")
    private String apiKey;

    
    @Autowired
    private kakaoLoginService kakaoLoginService;
    @Autowired
    private jwtService jwtService;

    public JSONObject showLoginPage() {
        System.out.println("kakaoService showLoginPage");
        return utillService.makeJson(true, kakaoLoginService.showLoingPage(apiKey));
    }
    public void tryKakaoLogin(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("tryKakaoLogin");
        uservo uservo =kakaoLoginService.tryLogin(request,apiKey);
        Map<String,Object>makeCookies=new HashMap<>();
        makeCookies.put("accessToken",jwtService.getAccessToken(uservo.getEmail()));
        makeCookies.put("refreshToken", jwtService.getRefreshToken());
        utillService.makeCookie(makeCookies, response);
    }
    
}
