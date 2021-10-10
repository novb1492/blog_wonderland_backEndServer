package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.example.demo.confrim.model.phone.tryConfrimRandNumDto;
import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.confrim.service.confrimService;
import com.example.demo.user.model.tryJoinDto;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class restcontroller {
    
    @Autowired
    private userService userService;
    @Autowired
    private confrimService confrimService;
    @PostMapping("/auth/test")
    public void test() {
        System.out.println("test");
    }
    @PostMapping("/auth/checkLogin")
    public JSONObject checkLogin(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("checkLogin restcontroller");
        return userService.checkLogin();
    }
    @PostMapping("/auth/sendSms")
    public JSONObject sendSms(@Valid @RequestBody trySendSmsDto trySendSmsDto ,HttpServletResponse response) {
        System.out.println("sendSms restcontroller");
        return confrimService.sendPhone(trySendSmsDto);
    }
    @PostMapping("/auth/checkRandNum")
    public JSONObject checkRandNum(@Valid @RequestBody tryConfrimRandNumDto tryConfrimRandNumDto ,HttpServletResponse response) {
        System.out.println("checkRandNum restcontroller");
        return confrimService.checkRandNum(tryConfrimRandNumDto);
    }
    @PostMapping("/auth/tryJoin")
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
        return utillService.makeJson(true, "message");
    }

}
