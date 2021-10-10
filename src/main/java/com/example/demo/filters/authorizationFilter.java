package com.example.demo.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.demo.jwt.service.jwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class authorizationFilter extends BasicAuthenticationFilter  {
    
    private jwtService jwtService;

    public authorizationFilter(AuthenticationManager authenticationManager,jwtService jwtService) {
        super(authenticationManager);
        this.jwtService=jwtService;
        
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
            try {
                int uid=jwtService.openJwt(accessToken);
            } catch (TokenExpiredException e) {
                System.out.println("토큰기간만료");
                goToError("/auth/jwtex", request, response);
            }catch(JWTDecodeException e){
                e.printStackTrace();
                System.out.println("토큰변환실패");
                goToError("/auth/failOpenToken", request, response);
            }
           
        }
    }
    private void goToError(String errorUrl,HttpServletRequest request,HttpServletResponse response) {
        System.out.println("goToError");
        RequestDispatcher dp=request.getRequestDispatcher(errorUrl);
        try {
            dp.forward(request, response);
        } catch (ServletException | IOException e) {
            System.out.println("에러링크 존재 하지 않음");
            e.printStackTrace();
        } 
    }
}
