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
    @Autowired
    private pointsDao pointsDao;

    @Transactional(rollbackFor = Exception.class)
    public JSONObject tryBuy(tryBuyDto tryBuyDto) {
        logger.info("tryBuy");
        List<Map<String,Object>>maps=getTotalPriceAndOther(tryBuyDto);
        logger.info(maps.toString());
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
            productVo productVo=productDao.findById(Integer.parseInt(itemArray[i][0].toString())).orElseThrow(()->new IllegalArgumentException("메세지: 존재하지 않는 상품입니다"));
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
                throw utillService.makeRuntimeEX("재고가 부족합니다 제품명:"+productName+" 최대 수량 "+dbCount+" 주문수량 "+tempCount, "getTotalPriceAndOther");
            }
            tempCount=0;
            LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap=new LinkedHashMap<>();
            confrimCoupon(couponName,count,eventmap,couponNamesAndCodeNames);
            confrimCode(codeName, count, eventmap,couponNamesAndCodeNames);
            logger.info(eventmap.toString()+" 코드쿠폰"+onlyPoint+"사용요청 포인트");
            onlyCash=getOnlyCash(productVo.getPrice(),count,eventmap,productVo.getMaxDiscountPercent(),result);
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
                maps.add(getTotalPrice(totalCash, onlyPoint,itemNames));
            }
        }

        return maps;

    }
    private Map<String,Object> getTotalPrice(int totalCash,int point,String itemNames){
        logger.info("getTotalPrice");
        Map<String,Object>map=new HashMap<>();
        int temp=totalCash-point;
        if(temp<0){
            temp=0;
            point=point-totalCash;
        }
        logger.info(temp+" 지불가격");
        map.put("totalCash", temp);
        map.put("totalPoint", point);
        map.put("itemNames", itemNames);
        return map;
    }
    private int getOnlyCash(int  price,int count,LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap,int maxDiscountPercent,Map<String,Object>result) {
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
                result.put("codeAction", codeAction);
                result.put("codeNum", codeNum);
                result.put("couponAction", couponAction);
                result.put("couponNum", couponNum);
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
                    totalDiscountPercent=((double)codeNum/price+(double)couponNum/price)*100;
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
            logger.info(tempPrice+" 할인가격");
            return tempPrice;
        } catch (RuntimeException e) {
            throw utillService.makeRuntimeEX(e.getMessage(), "getOnlyCash");
        }catch (Exception e) {
            throw utillService.makeRuntimeEX("금액계산에 실패했습니다", "getOnlyCash");
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
            logger.info("할인코드이 없습니다");
            return;
        }
        logger.info("할인코드 존재");
        String[] splite=codeName.split(",");
        if(splite.length>count){
            throw utillService.makeRuntimeEX("주문 개수보다 할인코드 개수가 많습니다", "getTotalPriceAndOther");
        }
        int temp=0;
            for(String s:splite){
                codesVo codesVo=codesDao.findByCodeName(s).orElseThrow(()->new IllegalArgumentException("메시지 : 존재하지 않는 할인코드입니다"));
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                if(LocalDateTime.now().isAfter(codesVo.getCdExpired().toLocalDateTime())){
                    throw utillService.makeRuntimeEX("기간이 지난 할인코드입니다", "getTotalPriceAndOther");
                }
                if(couponNamesAndCodeNames.contains(s)){
                    throw utillService.makeRuntimeEX("같은할인코드가 중복으로 발견되었습니다", "confrimCoupon");
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
            logger.info("코드액션 담기완료");
        logger.info("코드액션 담기완료");
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
            logger.info("쿠폰이 없습니다");
            return;
        }
        logger.info("쿠폰 존재");
        String[] splite=couponName.split(",");
        if(splite.length>count){
            throw utillService.makeRuntimeEX("주문 개수보다 쿠폰 개수가 많습니다", "getTotalPriceAndOther");
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
                if(couponNamesAndCodeNames.contains(s)){
                    throw utillService.makeRuntimeEX("같은쿠폰이 중복으로 발견되었습니다", "confrimCoupon");
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
    private int  confrimCount(Object count) {
        logger.info("confrimCount");
        int intcount=0;
        try {
            intcount=Integer.parseInt(count.toString());
        } catch (NumberFormatException e) {
            throw utillService.makeRuntimeEX("수량은 숫자여야합니다", "getTotalPriceAndOther");
        }
        if(intcount<=0){
            throw utillService.makeRuntimeEX("최소 주문수량은 0보다 커야합니다","getTotalPriceAndOther");
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
