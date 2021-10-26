package com.example.demo.jwt.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.jwt.model.jwtDao;
import com.example.demo.jwt.model.jwtVo;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.uservo;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class jwtService {
    private final static Logger LOGGER=LoggerFactory.getLogger(jwtService.class);

    @Value("${jwt.sing}")
    private String jwtSing;
    @Value("${jwt.access.name}")
    private String accessTokenName;
    @Value("${jwt.expiration}")
    private int accessTokenExpire;
    @Value("${jwt.refresh.name}")
    private String refreshTokenName;
    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpire;

    private final int secondOfDay=86400;
    
    @Autowired
    private jwtDao jwtDao;
    @Autowired
    private AuthenticationManager authenticationManager;

    public Authentication confrimEmailPwd(String email,String pwd,String provider) {
        LOGGER.info("confrimEmailPwd");
        if(provider!=null){
            pwd="oauthpwd";
        }
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pwd));
    }
    public void setSecuritySession(Authentication authentication) {
        LOGGER.info("setSecuritySession");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    public String getAccessToken(String email) {
        LOGGER.info("getAccessToken");
        LOGGER.info("토큰 email: "+email);
        return JWT.create().withSubject(accessTokenName).withExpiresAt(new Date(System.currentTimeMillis()+secondOfDay*accessTokenExpire)).withClaim("email",email).sign(Algorithm.HMAC512(jwtSing));
    }
    public String getRefreshToken() {
        LOGGER.info("getRefreshToken");
        return JWT.create().withSubject(refreshTokenName).withExpiresAt(new Date(System.currentTimeMillis()+secondOfDay*refreshTokenExpire)).sign(Algorithm.HMAC512(jwtSing));
    }
    @Transactional
    public void insert(uservo uservo,String refreshToken) {
        LOGGER.info("insert");
        jwtVo vo=jwtDao.findByTemail(uservo.getEmail());
        Timestamp expireDate=Timestamp.valueOf(LocalDateTime.now().plusDays(refreshTokenExpire));
        if(vo!=null){
            vo.setTcreated(Timestamp.valueOf(LocalDateTime.now()));
            vo.setTexpired(expireDate);
            vo.setTokenName(refreshToken);
            return;
        }
        vo=jwtVo.builder()
                .temail(uservo.getEmail())
                .texpired(expireDate)
                .tokenName(refreshToken)
                .tuid(uservo.getUid())
                .build();
                jwtDao.save(vo);
    }
    public String openJwt(String accessToken) {
        LOGGER.info("openJwt");
        return JWT.require(Algorithm.HMAC512(jwtSing)).build().verify(accessToken).getClaim("email").asString();
    }
    public Authentication makeAuthentication(principalDetail principalDetail) {
        LOGGER.info("makeAuthentication 로그인한 회원"+ principalDetail.getUservo().getEmail());
        return new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities());
    }
    public JSONObject reGetAccessToken(HttpServletRequest request,HttpServletResponse response ){
        LOGGER.info("reGetAccessToken");
        String refreshToken=findRefreshTokenAndGetEmail(utillService.getCookieValue(request, refreshTokenName)).orElseThrow(()->new IllegalArgumentException("재로그인 부탁드립니다"));
        Map<String,Object>map=new HashMap<>();
        map.put(accessTokenName,getAccessToken(refreshToken));
        utillService.makeCookie(map, response);
        return utillService.makeJson(false, "newAccessToken");
    }
    private Optional<String> findRefreshTokenAndGetEmail(String refreshToken) {
        LOGGER.info("findRefreshToken");
        try {
            LOGGER.info(refreshToken+" 리프레시토큰");
            jwtVo jwtVo=jwtDao.findByTokenName(refreshToken).orElseThrow(()->new IllegalArgumentException("리프레쉬 토큰 존재하지 않음"));
            jwtDao.updateTokenExpire(Timestamp.valueOf(LocalDateTime.now().plusDays(refreshTokenExpire)),Timestamp.valueOf(LocalDateTime.now()), refreshToken);
            return Optional.ofNullable(jwtVo.getTemail());
        } catch (Exception e) {
            return null;
        }
    }
    public void delete(String refreshToken) {
        LOGGER.info("delete");
        jwtDao.deleteByTokenName(refreshToken);
    }
}
