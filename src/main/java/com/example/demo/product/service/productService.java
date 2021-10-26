package com.example.demo.product.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.apis.settle.service.settleService;
import com.example.demo.payment.service.paymentService;
import com.example.demo.product.model.getProductInter;
import com.example.demo.product.model.productDao;
import com.example.demo.product.model.productVo;
import com.example.demo.product.model.tryBuyDto;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class productService {
    private final static Logger logger=LoggerFactory.getLogger(productService.class);
    private final int pageSize=10;
    private final int fullProductDay=7;
    @Autowired
    private productDao productDao;
    @Autowired
    private paymentService paymentService;
    @Autowired
    private settleService settleService;


    public JSONObject tryBuy(tryBuyDto tryBuyDto) {
        logger.info("tryBuy");
        String buyKind=tryBuyDto.getBuyKind();
        List<Map<String,Object>>maps=getTotalPriceAndOther(tryBuyDto.getBuy(), tryBuyDto.getKind());
        logger.info(maps.toString());
        /*if(buyKind.equals("vbank")){
            map.put("limitedDate", getVbankExpriedDate(tryBuyDto));
        }*/
        return settleService.makeBuyInfor(tryBuyDto, maps);
        
    }
    public JSONObject confrimPayment(HttpServletRequest request) {
        logger.info("confrimPayment");
        settleDto settleDto=utillService.requestToSettleDto(request);
        System.out.println(settleDto.toString());
        return paymentService.confrimPay(settleDto);
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
    private List<Map<String,Object>> getTotalPriceAndOther(int[][] itemArray,String kind) {
        logger.info("getTotalPriceAndOther");
        if(kind.equals("product")){
            return getTotalPriceAndOther(itemArray);
        }else {
            throw new RuntimeException("상품종류가 잘못되었습니다");
        }
    }
    private List<Map<String,Object>> getTotalPriceAndOther(int[][] itemArray) {
        logger.info("getTotalPriceAndOther");
        int itemArraySize=itemArray.length;
        int totalPrice=0;
        String itemNames="";
        int count=0;
        List<Map<String,Object>>maps=new ArrayList<>();
        for(int i=0;i<itemArraySize;i++){
            Map<String,Object>result=new HashMap<>();
            productVo productVo=productDao.findById(itemArray[i][0]).orElseThrow(()->new IllegalArgumentException("존재하지 않는 상품입니다"));
            if(itemArray[i][1]<=0){
                throw new RuntimeException("최소 주문수량은 0보다 커야합니다");
            }
            totalPrice+=getTotalPrice(productVo.getPrice(),itemArray[i][1]);
            itemNames+=productVo.getProductName();
            if(i<itemArraySize-1){
                itemNames=",";
            }
            count+=itemArray[i][1];
            if(count>productVo.getCount()){
                throw new RuntimeException("재고가 부족합니다");
            }
            result.put("itemName", productVo.getProductName());
            result.put("count", itemArray[i][1]);
            result.put("price", productVo.getPrice());
            maps.add(result);
            if(i==itemArraySize-1){
                result.clear();
                result.put("totalPrice", totalPrice);
                result.put("itemNames", itemNames);
                maps.add(result);
            }
        }

        return maps;

    }
    private int getTotalPrice(int  price, int count) {
        logger.info("getTotalPrice");
        return price*count;
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
