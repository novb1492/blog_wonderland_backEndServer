package com.example.demo.jwt.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.jwt.model.jwtDao;
import com.example.demo.jwt.model.jwtVo;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.uservo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class jwtService {

    @Value("${jwt.sing}")
    private String jwtSing;
    @Value("${jwt.access.name}")
    private String accessTokenName;
    @Value("${jwt.expiration}")
    private int accessTokenExpire;
    @Value("${jwt.refresh.name}")
    private String refreshTokenName;
    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpire;

    private final int secondOfDay=86400;
    
    @Autowired
    private jwtDao jwtDao;
    @Autowired
    private AuthenticationManager authenticationManager;

    public Authentication confrimEmailPwd(String email,String pwd,String provider) {
        System.out.println("confrimEmailPwd");
        if(provider!=null){
            pwd="oauthpwd";
        }
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pwd));
    }
    public void setSecuritySession(Authentication authentication) {
        System.out.println("setSecuritySession");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    public String getAccessToken(String email) {
        System.out.println("getAccessToken");
        System.out.println("토큰 email: "+email);
        return JWT.create().withSubject(accessTokenName).withExpiresAt(new Date(System.currentTimeMillis()+secondOfDay*accessTokenExpire)).withClaim("email",email).sign(Algorithm.HMAC512(jwtSing));
    }
    public String getRefreshToken() {
        System.out.println("getRefreshToken");
        return JWT.create().withSubject(refreshTokenName).withExpiresAt(new Date(System.currentTimeMillis()+secondOfDay*refreshTokenExpire)).sign(Algorithm.HMAC512(jwtSing));
    }
    @Transactional
    public void insert(uservo uservo,String refreshToken) {
        System.out.println("insert");
        jwtVo vo=jwtDao.findByTemail(uservo.getEmail());
        Timestamp expireDate=Timestamp.valueOf(LocalDateTime.now().plusDays(refreshTokenExpire));
        if(vo!=null){
            vo.setTcreated(Timestamp.valueOf(LocalDateTime.now()));
            vo.setTexpired(expireDate);
            vo.setTokenName(refreshToken);
            return;
        }
        vo=jwtVo.builder()
                .temail(uservo.getEmail())
                .texpired(expireDate)
                .tokenName(refreshToken)
                .tuid(uservo.getUid())
                .build();
                jwtDao.save(vo);
    }
    public String openJwt(String accessToken) {
        System.out.println("openJwt");
        return JWT.require(Algorithm.HMAC512(jwtSing)).build().verify(accessToken).getClaim("email").asString();
    }
    public Authentication makeAuthentication(principalDetail principalDetail) {
        System.out.println("makeAuthentication 로그인한 회원"+ principalDetail.getUservo().getName());
        return new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities());
    }
}
