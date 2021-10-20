package com.example.demo.apis.kakao.login;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.apis.requestTo;
import com.example.demo.enums.Stringenums;
import com.example.demo.user.model.uservo;
import com.example.demo.user.service.userService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class kakaoLoginService {
   
    private final static Logger LOGGER=LoggerFactory.getLogger(kakaoLoginService.class);
    @Value("${kakao.login.callback}")
    private String loginCallbackUrl;

    @Autowired
    private requestTo requestTo;
    @Autowired
    private userService userService;
   

    public String showLoingPage(String apikey) {
        LOGGER.info("kakaoLoginService showLoginPage");
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+loginCallbackUrl+"";
    }
    public uservo tryLogin(HttpServletRequest request,String apiKey) {
        LOGGER.info("tryLogin");
        JSONObject reseponseTokens=getKakaoToken(request.getParameter("code").toString(), apiKey);
        LOGGER.info(reseponseTokens.toString());
        JSONObject responseUserInfor=getUserInfor(reseponseTokens.getAsString("access_token").toString());
        LOGGER.info(responseUserInfor.toString());
        LinkedHashMap<String,Object>userInfor=(LinkedHashMap<String, Object>) responseUserInfor.get("kakao_account");
        uservo vo=mapToVo(userInfor);
        userService.insertOauth(vo);
        return vo;
    }
    private JSONObject getKakaoToken(String code,String apikey) {
        LOGGER.info("getKakaoToken");
        HttpHeaders headers=requestTo.getHeaders();
        MultiValueMap<String,Object> body=requestTo.getMultiValueBody();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        body.add("grant_type", "authorization_code");
        body.add("client_id", apikey);
        body.add("redirect_uri", loginCallbackUrl);
        body.add("code", code);
        return requestTo.requestToApi(body, "https://kauth.kakao.com/oauth/token", headers);
    }
    private JSONObject getUserInfor(String accessToken) {
        LOGGER.info("getUserInfor");
        HttpHeaders headers=requestTo.getHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization","Bearer "+accessToken);
        return requestTo.requestToApi("https://kapi.kakao.com/v2/user/me", headers);
    }
    private uservo mapToVo(LinkedHashMap<String,Object>userInfor) {
        LOGGER.info("mapToVo");
        LinkedHashMap<String,Object>profile=(LinkedHashMap<String, Object>) userInfor.get("profile");
        uservo vo=uservo.builder()
                        .address("테스트 주소 안줌")
                        .email((String)userInfor.get("email"))
                        .name((String)profile.get("nickname"))
                        .phoneNum("테스트 번호 안줌")
                        .provider("kakao")
                        .role(Stringenums.role_user.getString())
                        .build();
                        return vo;
    }
}
