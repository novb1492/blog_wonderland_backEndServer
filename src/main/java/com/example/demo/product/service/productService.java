package com.example.demo.product.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.product.model.getProductInter;
import com.example.demo.product.model.productDao;
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
    @Autowired
    private productDao productDao;
    
    public JSONObject getProducts(HttpServletRequest request) {
        LOGGER.info("getProducts");
        String kind=request.getParameter("kind");
        String keyword=request.getParameter("keyword");
        LOGGER.info("조회 품목:"+kind+"키워드 :"+keyword);
        int nowPage=Integer.parseInt(request.getParameter("page"));
        int start=utillService.getStart(nowPage, pageSize);
        List<getProductInter>productVos=getProductVos(kind, start, keyword);
        if(productVos.size()==0){
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
        return productDao.findByKindWithKeywordNative(kind, keyword, kind, keyword, start, pageSize);
    }
  
}
