package com.example.demo.product.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.apis.kakao.login.kakaoPayService;
import com.example.demo.apis.settle.service.settleService;
import com.example.demo.events.code.model.codesDao;
import com.example.demo.events.code.model.codesVo;
import com.example.demo.events.coupon.model.couponsDao;
import com.example.demo.events.coupon.model.couponsVo;
import com.example.demo.events.point.model.pointsDao;
import com.example.demo.events.point.model.pointsVo;
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
    private final int fullProductMin=10;
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
    @Autowired
    private pointsDao pointsDao;
    @Autowired
    private kakaoPayService kakaoPayService;

    @Transactional(rollbackFor = Exception.class)
    public JSONObject tryBuy(tryBuyDto tryBuyDto) {
        logger.info("tryBuy");
        List<Map<String,Object>>maps=getTotalPriceAndOther(tryBuyDto);
        logger.info(maps.toString());
        if(tryBuyDto.getBuyKind().equals("kakaoPay")){
            return kakaoPayService.getKaKaoPayLink(tryBuyDto, maps);
        }
        return settleService.makeBuyInfor(tryBuyDto, maps);
        
    }
    private List<Map<String,Object>> getTotalPriceAndOther(tryBuyDto tryBuyDto) {
        logger.info("getTotalPriceAndOther");
        String kind=tryBuyDto.getKind();
        if(kind.equals("product")){
            return getTotalPriceAndOther(tryBuyDto.getBuy(),tryBuyDto.getPoint(),tryBuyDto.getBuyKind(),tryBuyDto.getKind());
        }else {
            throw utillService.makeRuntimeEX("??????????????? ?????????????????????","getTotalPriceAndOther");
        }
    }
    private List<Map<String,Object>> getTotalPriceAndOther(Object[][] itemArray,String point,String buyKind,String kind) {
        logger.info("getTotalPriceAndOther");
        int itemArraySize=itemArray.length;
        String itemNames="";
        int onlyCash=0;
        int onlyPoint=0;
        int totalCash=0;
        String email=userService.sendUserInfor().getEmail();
        pointsVo pointsVo=pointsDao.findByPoEmail(email);
        List<Map<String,Object>>maps=new ArrayList<>();
        List<String>couponNamesAndCodeNames=new ArrayList<>();
        for(int i=0;i<itemArraySize;i++){
            Map<String,Object>result=new HashMap<>();
            String couponName=itemArray[i][2].toString();
            String codeName=itemArray[i][3].toString();
            int count=confrimCount(itemArray[i][1]);
            productVo productVo=productDao.findById(Integer.parseInt(itemArray[i][0].toString())).orElseThrow(()->new IllegalArgumentException("?????????: ???????????? ?????? ???????????????"));
            onlyPoint=confrimPoint(point,pointsVo.getPoHaving());
            String productName=productVo.getProductName();
            int dbCount=productVo.getCount();
            int tempCount=0;
                for(int ii=0;ii<itemArraySize;ii++){
                    if(Integer.parseInt(itemArray[i][0].toString())==Integer.parseInt(itemArray[ii][0].toString())){
                        tempCount+=Integer.parseInt(itemArray[ii][1].toString());
                    }
                    
                }
            if(tempCount>dbCount){
                throw utillService.makeRuntimeEX("????????? ??????????????? ?????????:"+productName+" ?????? ?????? "+dbCount+" ???????????? "+tempCount, "getTotalPriceAndOther");
            }
            tempCount=0;
            LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap=new LinkedHashMap<>();
            confrimCoupon(couponName,count,eventmap,couponNamesAndCodeNames);
            confrimCode(codeName, count, eventmap,couponNamesAndCodeNames);
            logger.info(eventmap.toString()+" ????????????"+onlyPoint+"???????????? ?????????");
            onlyCash=getOnlyCash(productVo.getPrice(),count,eventmap,productVo.getMaxDiscountPercent());
            totalCash+=onlyCash;
            itemNames+=productName;
            if(i<itemArraySize-1){
                itemNames+=",";
            }
            result.put("itemName",productName);
            result.put("count", count);
            result.put("price",onlyCash);
            result.put("bigKind",productVo.getBigKind());
            result.put("coupon", couponName);
            result.put("code", codeName);
            maps.add(result);
            if(i==itemArraySize-1){
                maps.add(getTotalPrice(totalCash, onlyPoint,itemNames,kind,buyKind));
            }
        }

        return maps;

    }
    private String getVbankExpriedDate(String kind) {
        logger.info("getVbankExpriedDate");
        if(kind.equals("reservation")){
            logger.info("???????????? ???????????? ??????");
            return null;
        }else{
            logger.info("???????????? ???????????? ??????");
            return utillService.getSettleVBankExpireDate(LocalDateTime.now().plusMinutes(fullProductMin).toString());
           
          
        }

    }
    private Map<String,Object> getTotalPrice(int totalCash,int point,String itemNames,String kind,String buyKind){
        logger.info("getTotalPrice");
        Map<String,Object>map=new HashMap<>();
        int temp=totalCash-point;
        if(temp<0){
            temp=0;
            point=point-totalCash;
        }
        logger.info(temp+" ????????????");
        map.put("totalCash", temp);
        map.put("totalPoint", point);
        map.put("itemNames", itemNames);
        if(buyKind.equals("vbank")){
            map.put("expireDate", getVbankExpriedDate(kind));
        }
        return map;
    }
    private int getOnlyCash(int  price,int count,LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap,int maxDiscountPercent) {
        logger.info("getOnlyCash");
        int tempPrice=0;
        try {
            for(int i=0;i<count;i++){
                String codeAction=(String)eventmap.get("code"+i).get("codeaction");
                logger.info(codeAction+" getOnlyCash");
                int codeNum=(int)eventmap.get("code"+i).get("codenum");
                logger.info(codeNum+" getOnlyCash");
                String couponAction=(String)eventmap.get("coupon"+i).get("couponaction");
                logger.info(couponAction+" getOnlyCash");
                int couponNum=(int)eventmap.get("coupon"+i).get("couponnum");
                logger.info(couponNum+" getOnlyCash");
                double totalDiscountPercent=0.00;
                if(codeAction.equals("percent")&&couponAction.equals("percent")){
                    logger.info("?????? ?????????");
                    totalDiscountPercent=codeNum+couponNum;
                }else if(codeAction.equals("percent")&&couponAction.equals("minus")){
                    logger.info("????????? ????????????");
                    totalDiscountPercent=codeNum+(double)couponNum/price*100;
                }else if(couponAction.equals("percent")&&codeAction.equals("minus")){
                    logger.info("????????? ????????????");
                    totalDiscountPercent=couponNum+(double)codeNum/price*100;
                }else if(couponAction.equals("minus")&&codeAction.equals("minus")){
                    logger.info("?????? ????????????");
                    totalDiscountPercent=((double)codeNum/price+(double)couponNum/price)*100;
                }else{
                    throw utillService.makeRuntimeEX("???????????? ??????????????? ????????????", "getTotalPrice");
                }
                logger.info(totalDiscountPercent+"?????? ?????????");
                if(maxDiscountPercent<totalDiscountPercent){
                    throw utillService.makeRuntimeEX("??? ????????? ?????? "+maxDiscountPercent+"%?????? ?????? ??????????????? ?????? "+totalDiscountPercent+"%", "getTotalPrice");
                }
                if(totalDiscountPercent>0.0){
                    tempPrice+=price-price*(totalDiscountPercent*0.01);
                }else{
                    tempPrice+=price;
                }
            }
            logger.info(tempPrice+" ????????????");
            return tempPrice;
        } catch (RuntimeException e) {
            throw utillService.makeRuntimeEX(e.getMessage(), "getOnlyCash");
        }catch (Exception e) {
            throw utillService.makeRuntimeEX("??????????????? ??????????????????", "getOnlyCash");
        }
      

    }
    private void confrimCode(String codeName,int count,LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap,List<String>couponNamesAndCodeNames) {
        logger.info("confrimCode");
        boolean flag=utillService.checkBlankOrNull(codeName);
        if(flag){
            for(int i=0;i<count;i++){
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                map.put("codeaction","minus");
                map.put("codenum",0);
                eventmap.put("code"+i, map);
            }
            logger.info("??????????????? ????????????");
            return;
        }
        logger.info("???????????? ??????");
        String[] splite=codeName.split(",");
        if(splite.length>count){
            throw utillService.makeRuntimeEX("?????? ???????????? ???????????? ????????? ????????????", "getTotalPriceAndOther");
        }
        int temp=0;
            for(String s:splite){
                codesVo codesVo=codesDao.findByCodeName(s).orElseThrow(()->new IllegalArgumentException("????????? : ???????????? ?????? ?????????????????????"));
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                if(LocalDateTime.now().isAfter(codesVo.getCdExpired().toLocalDateTime())){
                    throw utillService.makeRuntimeEX("????????? ?????? ?????????????????????", "getTotalPriceAndOther");
                }
                if(couponNamesAndCodeNames.contains(s)){
                    throw utillService.makeRuntimeEX("????????????????????? ???????????? ?????????????????????", "confrimCoupon");
                }
                couponNamesAndCodeNames.add(s);
                map.put("codeaction",codesVo.getCdKind());
                map.put("codenum",codesVo.getCdNum());
                eventmap.put("code"+temp, map);
                temp+=1;
            }
            if(temp<count){
                for(int i=temp;i<count;i++){
                    LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                    map.put("codeaction","minus");
                    map.put("codenum",0);
                    eventmap.put("code"+i, map);
                }
            }
            logger.info("???????????? ????????????");
    }
    private void confrimCoupon(String couponName,int count,LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap,List<String>couponNamesAndCodeNames){
        logger.info("confrimCoupon");
        boolean flag=utillService.checkBlankOrNull(couponName);
        if(flag){
            for(int i=0;i<count;i++){
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                    map.put("couponaction","minus");
                    map.put("couponnum",0);
                    eventmap.put("coupon"+i, map);
            }
            logger.info("????????? ????????????");
            return;
        }
        logger.info("?????? ??????");
        String[] splite=couponName.split(",");
        if(splite.length>count){
            throw utillService.makeRuntimeEX("?????? ???????????? ?????? ????????? ????????????", "getTotalPriceAndOther");
        }
        int temp=0;
            for(String s:splite){
                couponsVo couponsVo=couponsDao.findByCouponName(s).orElseThrow(()->new IllegalArgumentException("????????? : ???????????? ?????? ???????????????"));
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                if(LocalDateTime.now().isAfter(couponsVo.getCoExpired().toLocalDateTime())){
                    throw utillService.makeRuntimeEX("????????? ?????? ???????????????", "getTotalPriceAndOther");
                }else if(couponsVo.getUsedFlag()!=0){
                    throw utillService.makeRuntimeEX("?????? ????????? ???????????????", "getTotalPriceAndOther");
                }
                if(couponNamesAndCodeNames.contains(s)){
                    throw utillService.makeRuntimeEX("??????????????? ???????????? ?????????????????????", "confrimCoupon");
                }
                couponNamesAndCodeNames.add(s);
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
            logger.info("???????????? ????????????");
    }
      
    private int confrimPoint(String point,int dbPoint) {
        logger.info("confrimPoint");
        int onlyPoint=0;
        try {
            onlyPoint=Integer.parseInt(point);
        } catch (Exception e) {
            if(!point.equals("")){
                throw utillService.makeRuntimeEX("???????????? ????????????????????????","getTotalPriceAndOther");
            }
        }
        if(dbPoint<onlyPoint){
            throw utillService.makeRuntimeEX("???????????? ???????????????","getTotalPriceAndOther");
        }
        return onlyPoint;
    } 
    private int  confrimCount(Object count) {
        logger.info("confrimCount");
        int intcount=0;
        try {
            intcount=Integer.parseInt(count.toString());
        } catch (NumberFormatException e) {
            throw utillService.makeRuntimeEX("????????? ?????????????????????", "getTotalPriceAndOther");
        }
        if(intcount<=0){
            throw utillService.makeRuntimeEX("?????? ??????????????? 0?????? ???????????????","getTotalPriceAndOther");
        }
        return intcount;
    }
    public JSONObject selectProduct(HttpServletRequest request) {
        logger.info("selectProduct");
        String detail=request.getParameter("detail");
        if(detail.equals("all")){
            return getProducts(request);
        }else if(detail.equals("one")){
            return getProduct(request);
        }else{
            return utillService.makeJson(false, "??????????????? ???????????? ????????????");
        }
    }
    private JSONObject getProduct(HttpServletRequest request) {
        logger.info("getProduct");
        int id=Integer.parseInt(request.getParameter("id"));
        productVo productVo=productDao.findById(id).orElseGet(()->null);
        if(productVo==null){
            return utillService.makeJson(false, "???????????? ?????? ???????????????");
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
        logger.info("?????? ??????:"+kind+"????????? :"+keyword);
        int nowPage=Integer.parseInt(request.getParameter("page"));
        int start=utillService.getStart(nowPage, pageSize);
        List<getProductInter>productVos=getProductVos(kind, start, keyword);
        if(productVos.size()==0||utillService.checkBlankOrNull(kind)){
            return utillService.makeJson(false, "??????????????? ????????????");
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
            logger.info("????????? ????????? ??????");
            return productDao.findByKind(kind,kind,start-1,pageSize);
        }
        logger.info("?????? ????????? ??????");
        logger.info("?????????: "+keyword);
        return productDao.findByKindWithKeywordNative(kind, keyword, kind, keyword, start-1, pageSize);
    }
  
}
