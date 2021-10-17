package com.example.demo.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.userDao;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class authorizationFilter extends BasicAuthenticationFilter  {
    
    private jwtService jwtService;
    private userDao userDao;

    public authorizationFilter(AuthenticationManager authenticationManager,jwtService jwtService,userDao userDao) {
        super(authenticationManager);
        this.jwtService=jwtService;
        this.userDao=userDao;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws IOException, ServletException {
        System.out.println("doFilterInternal 페이지요청 발생 "+request.getRequestURI());
        String accessToken=null;
        try {
            accessToken=utillService.getCookieValue(request, "accessToken");
        } catch (Exception e) {
            System.out.println("토큰없음");
        }
        if(accessToken==null){
            chain.doFilter(request, response);
        }else{
            System.out.println("토큰발견");
            try {
                String email=jwtService.openJwt(accessToken);
                uservo uservo=userDao.findByEmail(email).orElseThrow(()->new IllegalArgumentException("잘못된 회원정보입니다"));
                jwtService.setSecuritySession(jwtService.makeAuthentication(new principalDetail(uservo)));
                chain.doFilter(request, response);
            } catch (TokenExpiredException e) {
                System.out.println("토큰기간만료");
                goToError("/user/jwtex", request, response);
            }catch(JWTDecodeException e){
                System.out.println("토큰변환실패");
                goToError("/user/failOpenToken", request, response);
            }catch(IllegalArgumentException e){
                System.out.println("존재하지 않는 회원");
                goToError("/user/failFindUser", request, response);
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
