package com.example.demo.apis;

import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class requestTo {
    private RestTemplate restTemplate=new RestTemplate();

    public JSONObject requestToKakao(MultiValueMap<String,Object> body,String url,HttpHeaders headers) {
        System.out.println("requestToKakao");
        try {
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            utillService.makeJson(false, "통신실패");
        }
        return null;
    }
    public JSONObject requestToKakao(JSONObject body,String url,HttpHeaders headers) {
        System.out.println("requestToKakao");
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            utillService.makeJson(false, "통신실패");
        }
        return null;
    }
    public JSONObject requestToKakao(String url,HttpHeaders headers) {
        System.out.println("requestToKakao");
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<>(headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            utillService.makeJson(false, "통신실패");
        }
        return null;
    }
}
