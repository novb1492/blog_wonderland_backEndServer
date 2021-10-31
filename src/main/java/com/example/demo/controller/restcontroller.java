package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.example.demo.apis.kakao.kakaoService;
import com.example.demo.apis.naver.naverService;
import com.example.demo.apis.settle.service.settleService;
import com.example.demo.confrim.model.phone.tryConfrimRandNumDto;
import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.find.service.findService;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.product.model.tryBuyDto;
import com.example.demo.product.service.productService;
import com.example.demo.send.snsService;
import com.example.demo.user.model.tryJoinDto;
import com.example.demo.user.model.tryUpadateDto;
import com.example.demo.user.service.userService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class restcontroller {
    private final static Logger LOGGER=LoggerFactory.getLogger(restcontroller.class);
    @Value("${front.domain}")
    private String frontDamain;
    
    @Autowired
    private userService userService;
    @Autowired
    private snsService snsService;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private naverService naverService;
    @Autowired
    private kakaoService kakaoService;
    @Autowired
    private findService findService;
    @Autowired
    private productService productService;
    @Autowired
    private settleService settleService;

    @RequestMapping(value = "/user/crud/**",method = RequestMethod.GET)
    public JSONObject checkLogin(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("checkLogin restcontroller");
        return userService.checkLogin(request);
    }
    @RequestMapping(value = "/user/change/**",method = RequestMethod.PUT)
    public JSONObject userChange(@Valid @RequestBody tryUpadateDto tryUpadateDto ,HttpServletResponse response) {
        LOGGER.info("userChange restcontroller");
        return userService.update(tryUpadateDto);
    }
    @RequestMapping(value = "/confrim/**",method = RequestMethod.POST)
    public JSONObject sendSms(@Valid @RequestBody trySendSmsDto trySendSmsDto ,HttpServletResponse response) {
        LOGGER.info("sendSms restcontroller");
        return snsService.sendNum(trySendSmsDto);
    }
    @RequestMapping(value = "/confrim/**",method = RequestMethod.PUT)
    public JSONObject checkRandNum(@Valid @RequestBody tryConfrimRandNumDto tryConfrimRandNumDto ,HttpServletResponse response) {
        LOGGER.info("checkRandNum restcontroller");
        return snsService.checkRandNum(tryConfrimRandNumDto);
    }
    @RequestMapping(value = "/user/crud/**",method = RequestMethod.POST)
    public JSONObject tryJoin(@Valid @RequestBody tryJoinDto tryJoinDto ,HttpServletResponse response) {
        LOGGER.info("tryJoin restcontroller");
        return userService.insert(tryJoinDto);
    }
    @PostMapping("/login")
    public JSONObject login(HttpServletRequest request ,HttpServletResponse response) {
        LOGGER.info("login restcontroller");
        return userService.checkSucLogin();
    }
    
    @RequestMapping(value = "/user/logout",method = RequestMethod.GET)
    public JSONObject logOut(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("logOut restcontroller");
        return userService.logOut(request, response);
    }
    @RequestMapping("/user/jwtex")
    public JSONObject jwtex(HttpServletRequest request ,HttpServletResponse response) {
        LOGGER.info("jwtex restcontroller");
        return jwtService.reGetAccessToken(request, response);
    }
    @GetMapping("/naver/showPage")
    public JSONObject showNaverLoginPage(HttpServletRequest request ,HttpServletResponse response) {
        LOGGER.info("showNaverLoginPage restcontroller");
        return naverService.getNaverLogin();
    }
    @GetMapping("/naver/loginCallback")
    public void naverLoginCallback(HttpServletRequest request ,HttpServletResponse response) {
        LOGGER.info("naverLoginCallback restcontroller");
        naverService.tryNaverLogin(request,response);
        doRedirect(response, frontDamain+"doneLogin?provider=naver");
    }
    @RequestMapping(value = "/kakao/showPage",method = RequestMethod.GET)
    public JSONObject showKakaoPage(HttpServletRequest request ,HttpServletResponse response) {
        LOGGER.info("showKakaoLoginPage restcontroller");
        return kakaoService.showPage(request);
    }
    @GetMapping("/kakao/callback/**")
    public void kakaoCallback(HttpServletRequest request ,HttpServletResponse response) {
        LOGGER.info("showKakaoLoginPage restcontroller");
        kakaoService.callback(request, response);
        doRedirect(response, frontDamain+"doneLogin?provider=kakao");
    }
    @RequestMapping(value = "/find/**",method = RequestMethod.GET)
    public JSONObject findSomthing(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("findSomthing");
        return findService.findRequest(request.getParameter("token"), request.getParameter("scope"));
    }
    @RequestMapping(value = "/product/select",method = RequestMethod.GET)
    public JSONObject getProducts(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("getProducts restcontroller");
        return productService.selectProduct(request);
    }
    @RequestMapping(value = "/api/product/buy/**",method = RequestMethod.POST)
    public JSONObject tryBuyProduct(@Valid @RequestBody tryBuyDto tryBuyDto,HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("tryBuyProduct restcontroller");
        jwtService.reGetAccessToken(request, response);
        return productService.tryBuy(tryBuyDto);
    }
    @RequestMapping(value = "/settle/callback",method = RequestMethod.POST)
    public void settleCallbck(HttpServletRequest request,HttpServletResponse response) {
        LOGGER.info("settleCallbck restcontroller"); 
        JSONObject result=settleService.confrimPayment(request);
        boolean flag=(boolean)result.get("flag");
        if(flag){
            doRedirect(response, frontDamain+"popUpClose?result="+flag+"&detail=settle&mchtTrdNo="+result.get("mchtTrdNo")+"&price="+result.get("price"));
        }else{
            doRedirect(response, frontDamain+"popUpClose?result="+flag+"&message="+result.get("message")+"&detail=settle");
        }

    }
    private void doRedirect(HttpServletResponse response,String url) {
        LOGGER.info("doRedirect");
        LOGGER.info(url+"리다이렉트 요청 url");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.info("doRedirect error"+e.getMessage());
        }
    }


}
