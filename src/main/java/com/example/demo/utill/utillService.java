package com.example.demo.utill;


import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.enums.Stringenums;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class utillService {

    private final static Logger logger=LoggerFactory.getLogger(utillService.class);
    
    public static JSONObject makeJson(Boolean flag,String message) {
        JSONObject response=new JSONObject();
        response.put(Stringenums.flag.getString(), flag);
        response.put(Stringenums.message.getString(), message);
        return response;
    }
    public static void throwRuntimeEX(Exception exception,String message,String methodName) {
        logger.info("throwRuntimeEX");
        logger.info(methodName+"error "+exception.getMessage());
        throw new RuntimeException(message);
    }
    public static RuntimeException makeRuntimeEX(String message,String methodName) {
        logger.info("throwRuntimeEX");
        logger.info(methodName+"error "+message);
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
        logger.info("makeCookie");
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
        logger.info("getCookieValue");
        Cookie[] cookies=request.getCookies();
        for(Cookie c:cookies){
            if(c.getName().equals(cookieName)){
                return c.getValue();
            }
        }
        return null;
    }
    public static void doRedirect(HttpServletResponse response,String url) {
        logger.info("doRedirect");
        logger.info(url+"리다이렉트 요청 url");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("doRedirect error"+e.getMessage());
        }
    }
    public static boolean checkBlankOrNull(String object) {
        logger.info("checkBlankOrNull");
        if(object.isBlank()||object==null||object.equals("null")){
            return true;
        }
        return false;
    }
    public static String checkLength(int min,int max,String object) {
        logger.info("checkLength");
        int length=object.length();
        if(length<min){
            return Stringenums.tooSmall.getString();
        }else if(length>max){
            return Stringenums.tooBig.getString();
        }
        return Stringenums.collect.getString();
    }
    public static boolean checkEquals(String object,String object2) {
        logger.info("checkEquals");
        if(object.equals(object2)){
            return true;
        }
        return false;
    }
    public static boolean checkEquals(int num,int num2) {
        logger.info("checkEquals");
        if(num==num2){
            return true;
        }
        return false;
    }
    public static void deleteCookie(String cookieName,HttpServletRequest request,HttpServletResponse response) {
        logger.info("deleteCookie");
        ResponseCookie cookie = ResponseCookie.from(cookieName, null) 
        .sameSite("None") 
        .secure(true) 
        .path("/") 
        .maxAge(0)
        .build(); 
        response.addHeader("Set-Cookie", cookie.toString()+";HttpOnly");
    }
    public static int getTotalPage(int totalCount,int pageSize) {
        logger.info("getTotalPage");
        int totalPage=totalCount/pageSize;
        int remainder=totalCount%pageSize;
        if(totalPage==0){
            return 1;
        }else if(remainder>0){
            totalPage+=1;
        }
        return totalPage;
    }
    public static int getStart(int page,int pageSize) {
        logger.info("getStart");
        logger.info("요청페이지 :"+page);
        return (page-1)*pageSize+1;
    }
    public static void comparePage(int nowPage,int totalPage) {
        logger.info("comparePage");
        if(nowPage>totalPage){
            throw new RuntimeException("마지막 페이지입니다");
        }
    }
    public static String getSettleText(String mchtid,String method,String mchtTrdNo,String requestDate,String requestTime,String totalPrice)  {
        return  String.format("%s%s%s%s%s%s%s",mchtid,method,mchtTrdNo,requestDate,requestTime,totalPrice,"ST1009281328226982205");
    }
    public static Map<String,String> getTrdDtTrdTm() {
        logger.info("getTrdDtTrdTm");
        Timestamp timestamp=Timestamp.valueOf(LocalDateTime.now());
        logger.info(timestamp+" 전체");
        String[] spl=timestamp.toString().split(" ");
        String trdDt=spl[0].replace("-","");
        logger.info(trdDt+" 요일");
        String min=LocalDateTime.now().getMinute()+"";
        String second=LocalDateTime.now().getSecond()+"";
        String hour=LocalDateTime.now().getHour()+"";
        if(hour.length()<2){
            hour="0"+hour;
        }
        if(min.length()<2){
            min="0"+min;
        }
        if(second.length()<2){
            second="0"+second;
        }
        String trdTm=hour+min+second;
        logger.info(trdTm+" 시간");
        Map<String,String>map=new HashMap<>();
        map.put("trdDt", trdDt);
        map.put("trdTm", trdTm);
        return map;
    }
}
