package com.example.demo.events.coupon.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.example.demo.events.coupon.model.couponsDao;
import com.example.demo.events.coupon.model.couponsVo;
import com.example.demo.utill.utillService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class couponService {

    private final static Logger logger=LoggerFactory.getLogger(couponService.class);
    
    @Autowired
    private couponsDao couponsDao;


    public void confrimCoupon(String couponName,int count,LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap){
        logger.info("confrimCoupon");
        boolean flag=utillService.checkBlankOrNull(couponName);
        if(flag){
            for(int i=0;i<count;i++){
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                    map.put("couponaction","minus");
                    map.put("couponnum",0);
                    eventmap.put("coupon"+i, map);
            }
            logger.info("쿠폰이 없습니다");
            return;
        }
        logger.info("쿠폰 존재");
        String[] splite=couponName.split(",");
        int size=splite.length;
        if(splite.length>count){
            throw utillService.makeRuntimeEX("주문 개수보다 쿠폰 개수가 많습니다", "getTotalPriceAndOther");
        }
        for(int i=0;i<size;i++){
            for(int ii=0;ii<size;ii++){
                if(i==ii){
                    continue;
                }
                if(splite[i].equals(splite[ii])){
                    throw utillService.makeRuntimeEX("동일쿠폰는 사용불가능합니다", "getTotalPriceAndOther");
                }
            }
        }
        int temp=0;
            for(String s:splite){
                couponsVo couponsVo=couponsDao.findByCouponName(s).orElseThrow(()->new IllegalArgumentException("메시지 : 존재하지 않는 쿠폰입니다"));
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                if(LocalDateTime.now().isAfter(couponsVo.getCoExpired().toLocalDateTime())){
                    throw utillService.makeRuntimeEX("기간이 지난 쿠폰입니다", "getTotalPriceAndOther");
                }else if(couponsVo.getUsedFlag()!=0){
                    throw utillService.makeRuntimeEX("이미 사용된 쿠폰입니다", "getTotalPriceAndOther");
                }
                map.put("couponaction",couponsVo.getCoKind());
                map.put("couponnum",couponsVo.getCoNum());
                eventmap.put("coupon"+temp, map);
                temp+=1;
            }
            if(temp<count){
                for(int i=temp;i<count;i++){
                    LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                    map.put("couponaction","minus");
                    map.put("couponnum",0);
                    eventmap.put("coupon"+i, map);
                }
            }
            logger.info("쿠폰액션 담기완료");
    }
    
}
