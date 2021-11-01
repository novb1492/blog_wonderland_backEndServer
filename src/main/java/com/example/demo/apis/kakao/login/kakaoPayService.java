package com.example.demo.apis.kakao.login;

import java.util.List;
import java.util.Map;

import com.example.demo.apis.requestTo;
import com.example.demo.payment.service.paymentService;
import com.example.demo.product.model.tryBuyDto;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;


@Service
public class kakaoPayService {
    private final static Logger logger=LoggerFactory.getLogger(kakaoPayService.class);
    private final String readyUrl="https://kapi.kakao.com/v1/payment/ready";
    private final String cid="TC0ONETIME";
    private final String callbackUrl="kakao/callback?scope=pay";
    @Value("${back.domain}")
    private  String backDomain;
    @Value("${kakao.Admin.key}")
    private String kakaoAdminKey;

    @Autowired
    private requestTo requestTo;
    @Autowired
    private userService userService;
    @Autowired
    private paymentService paymentService;

    @Transactional(rollbackFor = Exception.class)
    public JSONObject getKaKaoPayLink(tryBuyDto tryBuyDto,List<Map<String,Object>>maps) {
        logger.info("getKaKaoPayLink");
        MultiValueMap<String,Object> body=requestTo.getMultiValueBody();
        HttpHeaders headers=requestTo.getHeaders();
        String mchtTrdNo=maps.get(0).get("bigKind")+utillService.getRandomNum(10);
        Map<String,Object>map=maps.get(maps.size()-1);
        String email=userService.sendUserInfor().getEmail();
        body.add("cid", cid);
        body.add("partner_order_id",mchtTrdNo);
        body.add("partner_user_id",email);
        body.add("item_name",  map.get("itemNames"));
        body.add("quantity", 10);
        body.add("total_amount",map.get("totalCash"));
        body.add("tax_free_amount", 0);
        body.add("approval_url", backDomain+callbackUrl);
        body.add("cancel_url", backDomain+callbackUrl);
        body.add("fail_url", backDomain+callbackUrl);
        headers.add("Authorization","KakaoAK "+kakaoAdminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        paymentService.insertTemp(mchtTrdNo,email,tryBuyDto.getBuyKind(),(int)map.get("totalCash"),(int)map.get("totalPoint"),null);
        paymentService.insertTemp(maps, mchtTrdNo, email);
        JSONObject response=requestTo.requestToApi(body, readyUrl, headers);
        System.out.println(response);
        return utillService.makeJson(true,(String)response.get("next_redirect_pc_url"));
    }
 
}
