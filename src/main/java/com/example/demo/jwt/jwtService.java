package com.example.demo.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class jwtService {
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
}
