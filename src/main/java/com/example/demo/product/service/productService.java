package com.example.demo.product.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.apis.settle.service.paymentService;
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
    private final static Logger LOGGER=LoggerFactory.getLogger(productService.class);
    private final int pageSize=10;
    private final int fullProductDay=7;
    @Autowired
    private productDao productDao;
    @Autowired
    private paymentService paymentService;


    public JSONObject tryBuy(tryBuyDto tryBuyDto) {
        LOGGER.info("tryBuy");
        String buyKind=tryBuyDto.getBuyKind();
        Map<String,Object>map=getTotalPriceAndOther(tryBuyDto.getBuy(), tryBuyDto.getKind());
        LOGGER.info(map.toString());
        if(buyKind.equals("vbank")){
            map.put("limitedDate", getVbankExpriedDate(tryBuyDto));
        }
        return paymentService.makeBuyInfor(tryBuyDto,map);
        
    }
    public void confrimPayment(HttpServletRequest request) {
        LOGGER.info("confrimPayment");
        settleDto settleDto=utillService.requestToSettleDto(request);
        System.out.println(settleDto.toString());
    }
    private Timestamp getVbankExpriedDate(tryBuyDto tryBuyDto) {
        LOGGER.info("getVbankExpriedDate");
        if(tryBuyDto.getKind().equals("reservation")){
            LOGGER.info("예약상품 가상계좌 요청");
            return null;
        }else{
            LOGGER.info("일반상품 가상계좌 요청");
            return Timestamp.valueOf(LocalDateTime.now().plusDays(fullProductDay));
        }

    }
    private Map<String,Object> getTotalPriceAndOther(int[][] itemArray,String kind) {
        LOGGER.info("getTotalPriceAndOther");
        int itemArraySize=itemArray.length;
        int totalPrice=0;
        String itemName="";
        int count=0;
        List<Integer>timesOrSize=new ArrayList<>();
        Map<String,Object>result=new HashMap<>();
        for(int i=0;i<itemArraySize;i++){
            productVo productVo=productDao.findById(itemArray[i][0]).orElseThrow(()->new IllegalArgumentException("존재하지 않는 상품입니다"));
            if(itemArray[i][1]<=0){
                throw new RuntimeException("최소 주문수량은 0보다 커야합니다");
            }
            totalPrice+=getTotalPrice(productVo.getPrice(),itemArray[i][1]);
            itemName+=productVo.getProductName();
            if(i!=itemArraySize-1){
                itemName+=",";
            }
            count+=itemArray[i][1];
            if(count>productVo.getCount()){
                throw new RuntimeException("재고가 부족합니다");
            }
            if(kind.equals("reservation")){
                System.out.println("예약 상품 입니다 시간 분리 시작");
                timesOrSize.add(itemArray[i][2]);
                if(i==itemArraySize-1){
                    System.out.println("시간 분리 완료");
                    result.put("timesOrSize", timesOrSize);
                }
            }
        }
        
        result.put("totalPrice", totalPrice);
        result.put("itemName", itemName);
        result.put("count", count);
        return result;
    }
    private int getTotalPrice(int  price, int count) {
        LOGGER.info("getTotalPrice");
        return price*count;
    }
    public JSONObject selectProduct(HttpServletRequest request) {
        LOGGER.info("selectProduct");
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
        LOGGER.info("getProduct");
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
        LOGGER.info("getProducts");
        String kind=request.getParameter("kind");
        String keyword=request.getParameter("keyword");
        LOGGER.info("조회 품목:"+kind+"키워드 :"+keyword);
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
        LOGGER.info("getProductVos");
        if(utillService.checkBlankOrNull(keyword)){
            LOGGER.info("검색어 키워드 없음");
            return productDao.findByKind(kind,kind,start-1,pageSize);
        }
        LOGGER.info("검색 키워드 존재");
        LOGGER.info("키워드: "+keyword);
        return productDao.findByKindWithKeywordNative(kind, keyword, kind, keyword, start-1, pageSize);
    }
  
}
