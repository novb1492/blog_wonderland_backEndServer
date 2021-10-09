package com.example.demo.utill;


import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.enums.Stringenums;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class utillService {
    
    public static JSONObject makeJson(Boolean flag,String message) {
        JSONObject response=new JSONObject();
        response.put(Stringenums.flag.getString(), flag);
        response.put(Stringenums.message.getString(), message);
        return response;
    }
    public static void throwRuntimeEX(Exception exception,String message,String methodName) {
        System.out.println("throwRuntimeEX");
        System.out.println(methodName+"error "+exception.getMessage());
        throw new RuntimeException(message);
    }
    public static String getRandomNum(int end) {
        String num="";
        Random random=new Random();
        for(int i=0;i<end;i++){
            num+=Integer.toString(random.nextInt(10));
        }
        return num;
    } 
    public static void makeCookie(Map<String,Object>infor,HttpServletResponse response) {
        System.out.println("makeCookie");
        for(String key:infor.keySet()){
            ResponseCookie cookie = ResponseCookie.from(key,infor.get(key).toString()) 
            .sameSite("None") 
            .secure(true) 
            .path("/") 
            .build(); 
            if((boolean)infor.get("httpOnly")){
                response.addHeader("Set-Cookie", cookie.toString()+";HttpOnly");  
            }else{
                response.addHeader("Set-Cookie", cookie.toString()); 
            } 
        }
    }
}
