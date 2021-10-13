package com.example.demo.utill;


import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
            response.addHeader("Set-Cookie", cookie.toString()+";HttpOnly");  
        }
    }
    public static String getCookieValue(HttpServletRequest request,String cookieName) {
        System.out.println("getCookieValue");
        Cookie[] cookies=request.getCookies();
        for(Cookie c:cookies){
            if(c.getName().equals(cookieName)){
                return c.getValue();
            }
        }
        return null;
    }
    public static void doRedirect(HttpServletResponse response,String url) {
        System.out.println("doRedirect");
        System.out.println(url+"리다이렉트 요청 url");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("doRedirect error"+e.getMessage());
        }
    }
    public static boolean checkBlankOrNull(String object) {
        System.out.println("checkBlankOrNull");
        if(object.isBlank()||object==null){
            return true;
        }
        return false;
    }
    public static String checkLength(int min,int max,String object) {
        System.out.println("checkLength");
        int length=object.length();
        if(length<min){
            return Stringenums.tooSmall.getString();
        }else if(length>max){
            return Stringenums.tooBig.getString();
        }
        return Stringenums.collect.getString();
    }
    public static boolean checkEquals(String object,String object2) {
        System.out.println("checkEquals");
        if(object.equals(object2)){
            return true;
        }
        return false;
    }
}
