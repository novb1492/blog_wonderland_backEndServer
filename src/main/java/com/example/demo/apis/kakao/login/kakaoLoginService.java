package com.example.demo.apis.kakao.login;

import org.springframework.stereotype.Service;

@Service
public class kakaoLoginService {

    public String showLoingPage(String apikey,String loginCallbackUrl) {
        System.out.println("kakaoLoginService showLoginPage");
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+loginCallbackUrl+"";
    }
}
