package com.example.demo.confrim.service;

import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.stereotype.Service;

@Service
public class confrimService {
    
    public JSONObject sendPhone() {
        return utillService.makeJson(true, "인증번호 전송");
    }
}
