package com.example.demo.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class authorizationFilter extends BasicAuthenticationFilter  {

    public authorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws IOException, ServletException {
        System.out.println("doFilterInternal 페이지요청 발생");
        String accessToken=null;
        try {
            Cookie[] cookies=request.getCookies();
            for(Cookie c:cookies){
                if(c.getName().equals("accessToken")){
                    accessToken=c.getValue();
                }
            }
        } catch (Exception e) {
            System.out.println("토큰없음");
        }
        if(accessToken==null){
            chain.doFilter(request, response);
        }else{
            System.out.println("토큰발견");
        }
    }
}
