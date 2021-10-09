package com.example.demo.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.user.model.tryLoginDto;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class loginFilter extends UsernamePasswordAuthenticationFilter {
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
        System.out.println("loginFilter");
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            tryLoginDto userDto=objectMapper.readValue(request.getInputStream(), tryLoginDto.class);
            System.out.println("로그인시도 이메일: "+userDto.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    
    }
}
