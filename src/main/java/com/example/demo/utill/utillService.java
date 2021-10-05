package com.example.demo.utill;

import com.example.demo.enums.Stringenums;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.stereotype.Service;

@Service
public class utillService {
    
    public static JSONObject makeJson(Boolean flag,String message) {
        JSONObject response=new JSONObject();
        response.put(Stringenums.flag.getString(), flag);
        response.put(Stringenums.flag.getString(), message);
        return response;
    }
}
