package com.example.demo.jwt.service;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    public String makeAccessToken(String email) {
        return JWT.create().withSubject(accessTokenName).withExpiresAt(new Date(System.currentTimeMillis()+accessTokenExpire)).withClaim("id",email).sign(Algorithm.HMAC512(jwtSing));
    }
    public String refreshToken() {
        return JWT.create().withSubject(refreshTokenName).withExpiresAt(new Date(System.currentTimeMillis()+refreshTokenExpire)).sign(Algorithm.HMAC512(jwtSing));
    }
}
