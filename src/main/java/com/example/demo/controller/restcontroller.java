package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.example.demo.apis.naver.naverService;
import com.example.demo.confrim.model.phone.tryConfrimRandNumDto;
import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.confrim.service.confrimService;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.user.model.tryJoinDto;
import com.example.demo.user.service.userService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class restcontroller {
    
    @Autowired
    private userService userService;
    @Autowired
    private confrimService confrimService;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private naverService naverService;

    @RequestMapping(value = "/user/crud/**",method = RequestMethod.GET)
    public JSONObject checkLogin(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("checkLogin restcontroller");
        return userService.checkLogin(request);
    }
    @RequestMapping(value = "/send/**",method = RequestMethod.POST)
    public JSONObject sendSms(@Valid @RequestBody trySendSmsDto trySendSmsDto ,HttpServletResponse response) {
        System.out.println("sendSms restcontroller");
        return confrimService.sendPhone(trySendSmsDto);
    }
    @PostMapping("/user/checkNum")
    public JSONObject checkRandNum(@Valid @RequestBody tryConfrimRandNumDto tryConfrimRandNumDto ,HttpServletResponse response) {
        System.out.println("checkRandNum restcontroller");
        return confrimService.checkRandNum(tryConfrimRandNumDto);
    }
    @RequestMapping(value = "/user/crud/**",method = RequestMethod.POST)
    public JSONObject tryJoin(@Valid @RequestBody tryJoinDto tryJoinDto ,HttpServletResponse response) {
        System.out.println("tryJoin restcontroller");
        return userService.insert(tryJoinDto);
    }
    @PostMapping("/login")
    public JSONObject login(HttpServletRequest request ,HttpServletResponse response) {
        System.out.println("login restcontroller");
        return userService.checkSucLogin();
    }
    @PostMapping("/auth/jwtex")
    public JSONObject jwtex(HttpServletRequest request ,HttpServletResponse response) {
        System.out.println("jwtex restcontroller");
        return jwtService.reGetAccessToken(request, response);
    }
    @GetMapping("/auth/showNaverLoginPage")
    public JSONObject showNaverLoginPage(HttpServletRequest request ,HttpServletResponse response) {
        System.out.println("showNaverLoginPage restcontroller");
        return naverService.getNaverLogin();
    }
    @RequestMapping("/auth/naverLoginCallback")
    public void naverLoginCallback(HttpServletRequest request ,HttpServletResponse response) {
        System.out.println("naverLoginCallback restcontroller");
        naverService.tryNaverLogin(request,response);
        doRedirect(response, "http://localhost:3030/doneLogin");
    }
    private void doRedirect(HttpServletResponse response,String url) {
        System.out.println("doRedirect");
        System.out.println(url+"리다이렉트 요청 url");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("doRedirect error"+e.getMessage());
        }
    }

}
