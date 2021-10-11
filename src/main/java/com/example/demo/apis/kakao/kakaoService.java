package com.example.demo.apis.kakao;

import com.example.demo.apis.kakao.login.kakaoLoginService;
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
    @Value("${kakao.login.callback}")
    private String loginCallbackUrl;
    
    @Autowired
    private kakaoLoginService kakaoLoginService;

    public JSONObject showLoginPage() {
        System.out.println("kakaoService showLoginPage");
        return utillService.makeJson(true, kakaoLoginService.showLoingPage(apiKey, loginCallbackUrl));
    }
}
