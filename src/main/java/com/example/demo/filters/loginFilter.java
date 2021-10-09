package com.example.demo.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.jwt.service.jwtService;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.tryLoginDto;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class loginFilter extends UsernamePasswordAuthenticationFilter {

    private jwtService jwtService;

    public loginFilter(jwtService jwtService){
        this.jwtService=jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
        System.out.println("loginFilter");
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            tryLoginDto tryLoginDto=objectMapper.readValue(request.getInputStream(), tryLoginDto.class);
            System.out.println("로그인시도 이메일: "+tryLoginDto.getEmail());
            return jwtService.confrimEmailPwd(tryLoginDto.getEmail(),tryLoginDto.getPwd(),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 로그인성공");
        principalDetail principalDetail=(principalDetail)authResult.getPrincipal();
        uservo uservo=principalDetail.getUservo();
        Map<String,Object>makeCookies=new HashMap<>();
        makeCookies.put("accessToken",jwtService.makeAccessToken(uservo.getName()));
        makeCookies.put("httpOnly", true);
        utillService.makeCookie(makeCookies, response);
        chain.doFilter(request, response);
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,AuthenticationException failed) throws IOException, ServletException {
        System.out.println("successfulAuthentication 로그인실패");
    }
}
