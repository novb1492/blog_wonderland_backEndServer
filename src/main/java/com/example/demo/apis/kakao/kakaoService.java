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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class kakaoService {

    private final static Logger LOGGER=LoggerFactory.getLogger(kakaoService.class);
    @Value("${kakao.app.key}")
    private String appKey;
    @Value("${kakao.rest.key}")
    private String apiKey;
    @Value("${kakao.more.callback}")
    private String morePageCallback;
    @Value("${front.domain}")
    private String frontDamain;
    @Value("${jwt.access.name}")
    private String accessTokenName;
    @Value("${jwt.refresh.name}")
    private String refreshTokenName;

    
    @Autowired
    private kakaoLoginService kakaoLoginService;
    @Autowired
    private jwtService jwtService;

    public JSONObject showPage(HttpServletRequest request) {
        LOGGER.info("kakaoService showLoginPage");
        String scope=request.getParameter("scope");
        String url=null;
        if(scope.equals("login")){
            LOGGER.info("카카오 로그인 화면요청");
            url=kakaoLoginService.showLoingPage(apiKey);
        }else if(scope.equals("selfMessage")){
            LOGGER.info("카카오 추가동의 화면요청");
            url="https://kauth.kakao.com/oauth/authorize?client_id="+apiKey+"&redirect_uri="+morePageCallback+"&response_type=code&scope=talk_message";
        }
        return utillService.makeJson(true, url);
    }
    public void callback(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("callback");
        String scope=request.getParameter("scope");
        if(scope.equals("login")){
            LOGGER.info("카카오 로그인 콜백");
            tryKakaoLogin(request, response);
        }else if(scope.equals("more")){
            LOGGER.info("카카오 추가동의 콜백");
            utillService.doRedirect(response,frontDamain+"popUpClose");
        }else if(scope.equals("pay")){
            LOGGER.info("카카오페이 롤백");
        }else{
            throw utillService.makeRuntimeEX("카카오 콜백 잘못된 스코프입니다","callback");
        }
    }
    private void tryKakaoLogin(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("tryKakaoLogin");
        uservo uservo =kakaoLoginService.tryLogin(request,apiKey);
        Map<String,Object>makeCookies=new HashMap<>();
        makeCookies.put(accessTokenName,jwtService.getAccessToken(uservo.getEmail()));
        String refreshToken=jwtService.getRefreshToken();
        makeCookies.put(refreshTokenName, refreshToken);
        jwtService.insert(uservo, refreshToken);
        utillService.makeCookie(makeCookies, response);
    }
    
}
