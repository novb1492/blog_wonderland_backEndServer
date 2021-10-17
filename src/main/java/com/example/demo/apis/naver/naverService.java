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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class naverService {
    
    private RestTemplate restTemplate=new RestTemplate();
    private final String get="authorization_code";
    private final String update="refresh_token";
    private final String delete="delete";
    
    @Value("${naver.clientid}")
    private String naverId;
    @Value("${naver.secret}")
    private String pwd;
    @Value("${naver.loginCallback}")
    private String loginCallbackUrl;
    @Value("${jwt.access.name}")
    private String accessTokenName;
    @Value("${jwt.refresh.name}")
    private String refreshTokenName;

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
        makeCookies.put(accessTokenName,jwtService.getAccessToken(uservo.getEmail()));
        String refreshToken=jwtService.getRefreshToken();
        makeCookies.put(refreshTokenName, refreshToken);
        jwtService.insert(uservo, refreshToken);
        utillService.makeCookie(makeCookies, response);
    }
    private JSONObject getToken(String code,String state) {
        System.out.println("getToken");
        return restTemplate.getForObject("https://nid.naver.com/oauth2.0/token?grant_type="+get+"&client_id="+naverId+"&client_secret="+pwd+"&code="+code+"&state="+state+"", JSONObject.class);
    }
}
