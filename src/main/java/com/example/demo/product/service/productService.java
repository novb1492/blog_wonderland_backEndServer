package com.example.demo.product.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.apis.settle.service.settleService;
import com.example.demo.enums.Operator;
import com.example.demo.events.code.model.codesDao;
import com.example.demo.events.code.model.codesVo;
import com.example.demo.events.coupon.model.couponsDao;
import com.example.demo.events.coupon.model.couponsVo;
import com.example.demo.product.model.getPointAndProducts;
import com.example.demo.product.model.getProductInter;
import com.example.demo.product.model.productDao;
import com.example.demo.product.model.productVo;
import com.example.demo.product.model.tryBuyDto;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class productService {
    private final static Logger logger=LoggerFactory.getLogger(productService.class);
    private final int pageSize=10;
    private final int fullProductDay=7;
    @Autowired
    private productDao productDao;
    @Autowired
    private settleService settleService;
    @Autowired
    private userService userService;
    @Autowired
    private couponsDao couponsDao;
    @Autowired
    private codesDao codesDao;

    @Transactional(rollbackFor = Exception.class)
    public JSONObject tryBuy(tryBuyDto tryBuyDto) {
        logger.info("tryBuy");
        String buyKind=tryBuyDto.getBuyKind();
        List<Map<String,Object>>maps=getTotalPriceAndOther(tryBuyDto);
        logger.info(maps.toString());
        /*if(buyKind.equals("vbank")){
            map.put("limitedDate", getVbankExpriedDate(tryBuyDto));
        }*/
        return settleService.makeBuyInfor(tryBuyDto, maps);
        
    }
    private Timestamp getVbankExpriedDate(tryBuyDto tryBuyDto) {
        logger.info("getVbankExpriedDate");
        if(tryBuyDto.getKind().equals("reservation")){
            logger.info("예약상품 가상계좌 요청");
            return null;
        }else{
            logger.info("일반상품 가상계좌 요청");
            return Timestamp.valueOf(LocalDateTime.now().plusDays(fullProductDay));
        }

    }
    private List<Map<String,Object>> getTotalPriceAndOther(tryBuyDto tryBuyDto) {
        logger.info("getTotalPriceAndOther");
        String kind=tryBuyDto.getKind();
        if(kind.equals("product")){
            return getTotalPriceAndOther(tryBuyDto.getBuy(),tryBuyDto.getPoint());
        }else {
            throw utillService.makeRuntimeEX("상품종류가 잘못되었습니다","getTotalPriceAndOther");
        }
    }
    private List<Map<String,Object>> getTotalPriceAndOther(Object[][] itemArray,String point) {
        logger.info("getTotalPriceAndOther");
        int itemArraySize=itemArray.length;
        int totalPrice=0;
        String itemNames="";
        int onlyCash=0;
        int onlyPoint=0;
        int totalCash=0;
        int totalPoint=0;
        String email=userService.sendUserInfor().getEmail();
        List<Map<String,Object>>maps=new ArrayList<>();
        for(int i=0;i<itemArraySize;i++){
            Map<String,Object>result=new HashMap<>();
            String couponName=itemArray[i][2].toString();
            String codeName=itemArray[i][3].toString();
            getPointAndProducts getPointAndProducts=productDao.findProductJoinPoints(email,Integer.parseInt(itemArray[i][0].toString()));
            onlyPoint=confrimPoint(point,getPointAndProducts.getPo_having());
            if(utillService.checkBlankOrNull(getPointAndProducts.getProduct_name())){
                throw utillService.makeRuntimeEX("상품이 존재하지 않습니다", "getTotalPriceAndOther");
            }
            int count=Integer.parseInt(itemArray[i][1].toString());
            confrimCount(count, getPointAndProducts.getCount());
            LinkedHashMap<String,Map<String,Object>>eventmap=new LinkedHashMap<>();
            confrimCoupon(couponName,count,eventmap);
            confrimCode(codeName, count, eventmap);
            logger.info(eventmap.toString()+" 코드쿠폰"+onlyPoint+"사용요청 포인트");
            onlyCash=getOnlyCash(getPointAndProducts.getPrice(),count,onlyPoint,eventmap,getPointAndProducts.getMax_discount_percent());
           
            /*String codeName=itemArray[i][3].toString();
            onlyCash=getOnlyCash(getEventsAndProducts.getPrice(),count,onlyPoint,eventmap,getEventsAndProducts.getMax_discount_percent());
            totalCash+=onlyCash;
            int price=onlyCash+onlyPoint;
            totalPrice+=onlyCash+onlyPoint;
            itemNames+=getEventsAndProducts.getProduct_name();
            if(i<itemArraySize-1){
                itemNames=",";
            }
            result.put("coupone", couponName);
            result.put("code", codeName);
            result.put("itemName", getEventsAndProducts.getProduct_name());
            result.put("count", count);
            result.put("price",price);
            result.put("bigKind",getEventsAndProducts.getBig_kind());
            result.put("coupon", couponName);
            result.put("code", codeName);
            result.put("onlyCash",onlyCash);
            result.put("onlyPoint",onlyPoint);
            maps.add(result);
            if(i==itemArraySize-1){
                Map<String,Object>map2=new HashMap<>();
                map2.put("totalPrice", totalPrice);
                map2.put("totalCash", totalCash);
                map2.put("totalPoint", totalPoint);
                map2.put("itemNames", itemNames);
                maps.add(map2);
            }*/
        }

        return maps;

    }
    private int getOnlyCash(int  price,int count,int point,LinkedHashMap<String,Map<String,Object>>eventmap,int maxDiscountPercent) {
        logger.info("getOnlyCash");
        logger.info(eventmap.toString()+" getOnlyCash");
        int tempPrice=0;
        for(int i=0;i<count;i++){
            String codeAction=(String)eventmap.get("code"+i).get("codeaction");
            System.out.println(codeAction+" getOnlyCash");
            int codeNum=(int)eventmap.get("code"+i).get("codenum");
            System.out.println(codeNum+" getOnlyCash");
            String couponAction=(String)eventmap.get("coupon"+i).get("couponaction");
            System.out.println(couponAction+" getOnlyCash");
            int couponNum=(int)eventmap.get("coupon"+i).get("couponnum");
            System.out.println(couponNum+" getOnlyCash");
            double totalDiscountPercent=0;
            if(codeAction.equals("percent")&&couponAction.equals("percent")){
                logger.info("둘다 퍼센트");
                totalDiscountPercent=codeNum+couponNum;
            }else if(codeAction.equals("percent")&&couponAction.equals("minus")){
                logger.info("쿠폰만 마이너스");
                totalDiscountPercent=codeNum+(double)couponNum/price*100;
            }else if(couponAction.equals("percent")&&codeAction.equals("minus")){
                logger.info("코드만 마이너스");
                totalDiscountPercent=couponNum+(double)codeNum/price*100;
            }else if(couponAction.equals("minus")&&codeAction.equals("minus")){
                logger.info("둘다 마이너스");
                totalDiscountPercent=codeNum/price+couponNum/price;
            }else{
                throw utillService.makeRuntimeEX("지원하는 할인방법이 아닙니다", "getTotalPrice");
            }
            logger.info(totalDiscountPercent+"할인 페센트");
            if(maxDiscountPercent<totalDiscountPercent){
                throw utillService.makeRuntimeEX("이 상품은 최대 "+maxDiscountPercent+"%까지 할인 가능합니다 현재 "+totalDiscountPercent+"%", "getTotalPrice");
            }
            if(totalDiscountPercent>0.0){
                tempPrice+=price-price*(totalDiscountPercent*0.01);
            }else{
                tempPrice+=price;
            }
        }
        System.out.println(tempPrice-point+" 할인가격");
        return tempPrice-point;
    }
    private void confrimCode(String codeName,int count,LinkedHashMap<String,Map<String,Object>>eventmap) {
        logger.info("confrimCode");
        boolean flag=utillService.checkBlankOrNull(codeName);
        if(flag){
            for(int i=0;i<count;i++){
                Map<String,Object>map=new HashMap<>();
                map.put("codeaction","minus");
                map.put("codenum",0);
                eventmap.put("code"+i, map);
            }
            logger.info("할인코드이 없습니다");
            return;
        }
        logger.info("할인코드 존재");
        String[] splite=codeName.split(",");
        for(String s:splite){
            for(String ss:splite){
                if(ss.equals(s)){
                    throw utillService.makeRuntimeEX("동일코드는 사용불가능합니다", "getTotalPriceAndOther");
                }
            }
        }
        if(splite.length>count){
            throw utillService.makeRuntimeEX("주문 개수보다 할인코드 개수가 많습니다", "getTotalPriceAndOther");
        }
        int temp=0;
            for(String s:splite){
                codesVo codesVo=codesDao.findByCodeName(s).orElseThrow(()->new IllegalArgumentException("메시지 : 존재하지 않는 할인코드입니다"));
                Map<String,Object>map=new HashMap<>();
                if(LocalDateTime.now().isAfter(codesVo.getCdExpired().toLocalDateTime())){
                    throw utillService.makeRuntimeEX("기간이 지난 할인코드입니다", "getTotalPriceAndOther");
                }
                map.put("codeaction",codesVo.getCdKind());
                map.put("codenum",codesVo.getCdNum());
                eventmap.put("code"+temp, map);
                temp+=1;
            }
            if(temp<count){
                for(int i=temp;i<count;i++){
                    Map<String,Object>map=new HashMap<>();
                    map.put("codeaction","minus");
                    map.put("codenum",0);
                    eventmap.put("code"+i, map);
                }
            }
            logger.info("코드액션 담기완료");
        logger.info("코드액션 담기완료");
    }
    private void confrimCoupon(String couponName,int count,LinkedHashMap<String,Map<String,Object>>eventmap){
        logger.info("confrimCoupon");
        boolean flag=utillService.checkBlankOrNull(couponName);
        if(flag){
            for(int i=0;i<count;i++){
                Map<String,Object>map=new HashMap<>();
                    map.put("couponaction"+i,"minus");
                    map.put("couponnum"+i,0);
                    eventmap.put("coupon"+i, map);
            }
            logger.info("쿠폰이 없습니다");
            return;
        }
        logger.info("쿠폰 존재");
        String[] splite=couponName.split(",");
        if(splite.length>count){
            throw utillService.makeRuntimeEX("주문 개수보다 쿠폰 개수가 많습니다", "getTotalPriceAndOther");
        }
        for(String s:splite){
            for(String ss:splite){
                if(ss.equals(s)){
                    throw utillService.makeRuntimeEX("동일쿠폰은 사용불가능합니다", "getTotalPriceAndOther");
                }
            }
        }
        int temp=0;
            for(String s:splite){
                couponsVo couponsVo=couponsDao.findByCouponName(s).orElseThrow(()->new IllegalArgumentException("메시지 : 존재하지 않는 쿠폰입니다"));
                Map<String,Object>map=new HashMap<>();
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
                    Map<String,Object>map=new HashMap<>();
                    map.put("couponaction","minus");
                    map.put("couponnum",0);
                    eventmap.put("coupon"+i, map);
                }
            }
            logger.info("쿠폰액션 담기완료");
    }
      
    private int confrimPoint(String point,int dbPoint) {
        logger.info("confrimPoint");
        int onlyPoint=0;
        try {
            onlyPoint=Integer.parseInt(point);
        } catch (Exception e) {
            if(!point.equals("")){
                throw utillService.makeRuntimeEX("포인트는 숫자만가능합니다","getTotalPriceAndOther");
            }
        }
        if(dbPoint<onlyPoint){
            throw utillService.makeRuntimeEX("포인트가 부족합니다","getTotalPriceAndOther");
        }
        return onlyPoint;
    } 
    private void confrimCount(int count,int dbCount) {
        logger.info("confrimCount");
        if(count<=0){
            throw utillService.makeRuntimeEX("최소 주문수량은 0보다 커야합니다","getTotalPriceAndOther");
        }
        if(count>dbCount){
            throw utillService.makeRuntimeEX("재고가 부족합니다","getTotalPriceAndOther");
        }
        logger.info("개수 유효성 통과");
    }
    public JSONObject selectProduct(HttpServletRequest request) {
        logger.info("selectProduct");
        String detail=request.getParameter("detail");
        if(detail.equals("all")){
            return getProducts(request);
        }else if(detail.equals("one")){
            return getProduct(request);
        }else{
            return utillService.makeJson(false, "디테일값이 존재하지 않습니다");
        }
    }
    private JSONObject getProduct(HttpServletRequest request) {
        logger.info("getProduct");
        int id=Integer.parseInt(request.getParameter("id"));
        productVo productVo=productDao.findById(id).orElseGet(()->null);
        if(productVo==null){
            return utillService.makeJson(false, "존재하지 않는 상품입니다");
        }
        JSONObject response=new JSONObject();
        response.put("name", productVo.getProductName());
        response.put("size",productVo.getProductName().split("-")[1]);
        response.put("price", productVo.getPrice());
        response.put("img", productVo.getProductImg());
        response.put("count", productVo.getCount());
        response.put("flag", true);
        return response;

    }
    private JSONObject getProducts(HttpServletRequest request) {
        logger.info("getProducts");
        String kind=request.getParameter("kind");
        String keyword=request.getParameter("keyword");
        logger.info("조회 품목:"+kind+"키워드 :"+keyword);
        int nowPage=Integer.parseInt(request.getParameter("page"));
        int start=utillService.getStart(nowPage, pageSize);
        List<getProductInter>productVos=getProductVos(kind, start, keyword);
        if(productVos.size()==0||utillService.checkBlankOrNull(kind)){
            return utillService.makeJson(false, "검색결과가 없습니다");
        }
        int totalPage=utillService.getTotalPage(productVos.get(0).getTotalcount(), pageSize);
        utillService.comparePage(nowPage, totalPage);
        JSONObject response=new JSONObject();
        List<JSONObject>products=new ArrayList<>();
        for(getProductInter p: productVos){
            JSONObject product=new JSONObject();
            product.put("id", p.getPid());
            product.put("name", p.getProduct_name());
            product.put("price", p.getPrice());
            product.put("count", p.getCount());
            product.put("imgPath", p.getProduct_img());
            products.add(product);
        }
        response.put("flag", true);
        response.put("totalPage", totalPage);
        response.put("products", products);
        return response;
    }
    private List<getProductInter> getProductVos(String kind,int start,String keyword) {
        logger.info("getProductVos");
        if(utillService.checkBlankOrNull(keyword)){
            logger.info("검색어 키워드 없음");
            return productDao.findByKind(kind,kind,start-1,pageSize);
        }
        logger.info("검색 키워드 존재");
        logger.info("키워드: "+keyword);
        return productDao.findByKindWithKeywordNative(kind, keyword, kind, keyword, start-1, pageSize);
    }
  
}
