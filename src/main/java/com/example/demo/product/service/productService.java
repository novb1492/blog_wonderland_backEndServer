package com.example.demo.product.service;

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
        for(productVo p: productVos){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("name", p.getProductName());
            jsonObject.put("price", p.getPrice());
            jsonObject.put("count", p.getCount());
            jsonObject.put("imgPath", p.getProductImg());
            response.put("products", jsonObject);
        }
        return response;
    }
}
