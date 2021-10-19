package com.example.demo.product.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.product.model.getProductInter;
import com.example.demo.product.model.productDao;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class productService {
    private final int pageSize=10;
    @Autowired
    private productDao productDao;
    
    public JSONObject getProducts(HttpServletRequest request) {
        System.out.println("getProducts");
        String kind=request.getParameter("kind");
        System.out.println("조회 품목:"+kind);
        int nowPage=Integer.parseInt(request.getParameter("page"));
        int start=utillService.getStart(nowPage, pageSize);
        List<getProductInter>productVos=productDao.findByKind(kind,kind,start-1,pageSize).orElseThrow(()->new IllegalArgumentException("존재하지 않는 품목입니다"));
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
        response.put("totalPage", totalPage);
        response.put("products", products);
        return response;
    }
  
}
