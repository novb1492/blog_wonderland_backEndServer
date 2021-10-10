package com.example.demo.apis.naver;




import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.demo.apis.naver.login.naverLoginService;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class naverService {
    
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private final String get="authorization_code";
    private final String update="refresh_token";
    private final String delete="delete";
    
    @Value("${naver.clientid}")
    private String naverId;
    @Value("${naver.secret}")
    private String pwd;
    @Value("${naver.loginCallback}")
    private String loginCallbackUrl;

    @Autowired
    private naverLoginService naverLoginService;
    @Autowired
    private jwtService jwtService;

    public JSONObject getNaverLogin() {
       System.out.println("getNaverLogin");
       return naverLoginService.getNaverLogin(naverId);
    }
    public void tryNaverLogin(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("tryNaverLogin naverService");
        uservo uservo=naverLoginService.tryNaverLogin(getToken(request.getParameter("code"), request.getParameter("state")), response);
        Map<String,Object>makeCookies=new HashMap<>();
        makeCookies.put("accessToken",jwtService.getAccessToken(uservo.getEmail()));
        makeCookies.put("refreshToken", jwtService.getRefreshToken());
        utillService.makeCookie(makeCookies, response);
    }
    private JSONObject getToken(String code,String state) {
        System.out.println("getToken");
        return restTemplate.getForObject("https://nid.naver.com/oauth2.0/token?grant_type="+get+"&client_id="+naverId+"&client_secret="+pwd+"&code="+code+"&state="+state+"", JSONObject.class);
    }
    public JSONObject requestToNaver(String accessToken,String requestUrl) {
        System.out.println("requestToNaver"+accessToken);
        try {
            headers.add("Authorization","Bearer " + accessToken);
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(headers);
            return restTemplate.postForObject(requestUrl, entity, JSONObject.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return utillService.makeJson(false, "네이버 통신에 실패했습니다");
        }finally{
            headers.clear();
        }
    }
    

}
