package com.example.demo.apis;

import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger logger=LoggerFactory.getLogger(requestTo.class);
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> multiValueBody=new LinkedMultiValueMap<>();
    private JSONObject jsonBody=new JSONObject();

    public JSONObject requestToApi(MultiValueMap<String,Object> body,String url,HttpHeaders headers) {
        logger.info("requestToApi");
        try {
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            System.out.println(entity.toString());
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            e.getStackTrace();
            throw utillService.makeRuntimeEX("통신에 실패하였습니다", "requestToApi");
        }finally{
            body.clear();
            headers.clear();
        }
    }
    public JSONObject requestToApi(JSONObject body,String url,HttpHeaders headers) {
        logger.info("requestToApi");
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            e.getStackTrace();
            throw utillService.makeRuntimeEX("통신에 실패하였습니다", "requestToApi");
        }finally{
            body.clear();
            headers.clear();
        }
      
    }
    public JSONObject requestToApi(String url,HttpHeaders headers) {
        logger.info("requestToApi");
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<>(headers);
            return restTemplate.postForObject(url, entity, JSONObject.class);
        } catch (Exception e) {
            e.getStackTrace();
            throw utillService.makeRuntimeEX("통신에 실패하였습니다", "requestToApi");
        }finally{
            headers.clear();
        }
    }
    public JSONObject requestToSettle(String url,JSONObject body) {
        logger.info("reuqestToSettle");
        try {
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set("charset", "UTF-8");
      
            HttpEntity<JSONObject>entity=new HttpEntity<>(body,headers);
            logger.info(entity.getBody()+" 요청정보"+entity.getHeaders());
            JSONObject response= restTemplate.postForObject(url,entity,JSONObject.class);
            logger.info(response+" 세틀뱅크 통신결과");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("requestToSettle error "+ e.getMessage());
            throw utillService.makeRuntimeEX("통신에 실패하였습니다", "requestToApi");
        }finally{
            body.clear();
            headers.clear();
        }
    }
}
