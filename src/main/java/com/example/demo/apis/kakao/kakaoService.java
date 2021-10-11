package com.example.demo.apis.kakao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.apis.kakao.login.kakaoLoginService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class kakaoService {
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    
    @Value("${kakao.app.key}")
    private String appKey;
    @Value("${kakao.rest.key}")
    private String apiKey;

    
    @Autowired
    private kakaoLoginService kakaoLoginService;

    public JSONObject showLoginPage() {
        System.out.println("kakaoService showLoginPage");
        return utillService.makeJson(true, kakaoLoginService.showLoingPage(apiKey));
    }
    public void tryKakaoLogin(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("tryKakaoLogin");
        kakaoLoginService.tryLogin(request, response,apiKey);
    }
    public JSONObject requestToKakao(MultiValueMap<String,Object> body,String url) {
        System.out.println("requestToKakao");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            utillService.makeJson(false, "카카오 통신실패");
        }
        return null;
    }
   
}
