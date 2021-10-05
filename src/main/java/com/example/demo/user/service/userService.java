package com.example.demo.user.service;

import java.util.Optional;

import com.example.demo.enums.Stringenums;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class userService {
    private final String data=Stringenums.data.getString();

    public JSONObject checkLogin() {
        System.out.println("checkLogin");
        JSONObject response=utillService.makeJson(true,"로그인 사용자입니다" );
        response.put(data, sendUserInfor());
        return response;
        
    }
    public uservo sendUserInfor() {
        System.out.println("sendUserInfor");
        uservo uservo=new uservo();
        uservo.setEmail("kim@kim.com");
        uservo.setPwd("1111");
        uservo.setRole("user");

        principalDetail principaldetail=new principalDetail(uservo);
        Authentication authentication2=new UsernamePasswordAuthenticationToken(principaldetail,null,principaldetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication2);
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        principalDetail principalDetail2=(principalDetail)authentication.getPrincipal();
        Optional<uservo>optional=Optional.ofNullable(principalDetail2.getUservo());
        optional.orElseThrow(()->new RuntimeException("비로그인 사용자입니다"));
        return optional.get();
    }
}
