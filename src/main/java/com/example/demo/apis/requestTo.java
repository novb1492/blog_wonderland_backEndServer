package com.example.demo.apis;

import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
@Service
@Getter
public class requestTo {
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> multiValueBody=new LinkedMultiValueMap<>();
    private JSONObject jsonBody=new JSONObject();

    public JSONObject requestToApi(MultiValueMap<String,Object> body,String url,HttpHeaders headers) {
        System.out.println("requestToApi");
        try {
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            utillService.makeJson(false, "통신실패");
        }finally{
            multiValueBody.clear();
            headers.clear();
        }
        return null;
    }
    public JSONObject requestToApi(JSONObject body,String url,HttpHeaders headers) {
        System.out.println("requestToApi");
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            utillService.makeJson(false, "통신실패");
        }finally{
            jsonBody.clear();
            headers.clear();
        }
        return null;
    }
    public JSONObject requestToApi(String url,HttpHeaders headers) {
        System.out.println("requestToApi");
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<>(headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            utillService.makeJson(false, "통신실패");
        }finally{
            headers.clear();
        }
        return null;
    }
    public JSONObject requestToSettle(String url,JSONObject body) {
        System.out.println("reuqestToSettle");
        try {
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set("charset", "UTF-8");
      
            HttpEntity<JSONObject>entity=new HttpEntity<>(body,headers);
            System.out.println(entity.getBody()+" 요청정보"+entity.getHeaders());
            JSONObject response= restTemplate.postForObject(url,entity,JSONObject.class);
            System.out.println(response+" 세틀뱅크 통신결과");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("requestToSettle error "+ e.getMessage());
            throw new RuntimeException("세틀뱅크 통신 실패");
        }finally{
            body.clear();
            headers.clear();
        }
    }
}
