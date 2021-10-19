package com.example.demo.product.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.product.model.productDao;
import com.example.demo.product.model.productVo;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class productService {
    
    @Autowired
    private productDao productDao;
    
    public JSONObject getProducts(HttpServletRequest request) {
        System.out.println("getProducts");
        String kind=request.getParameter("kind");
        System.out.println("조회 품목:"+kind);
        List<productVo>productVos=productDao.findByKind(kind).orElseThrow(()->new IllegalArgumentException("존재하지 않는 품목입니다"));
        JSONObject response=new JSONObject();
        List<JSONObject>products=new ArrayList<>();
        for(productVo p: productVos){
            JSONObject product=new JSONObject();
            product.put("name", p.getProductName());
            product.put("price", p.getPrice());
            product.put("count", p.getCount());
            product.put("imgPath", p.getProductImg());
            products.add(product);
        }
        response.put("products", products);
        return response;
    }
}
