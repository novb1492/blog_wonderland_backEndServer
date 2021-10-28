package com.example.demo.product.model;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface productDao extends JpaRepository<productVo,Integer> {

    @Query(value = "select *,(select count(*) from products where kind=?)totalcount from products where kind=?  order by pid asc limit ?,?",nativeQuery = true)
    List<getProductInter> findByKind(String kind,String kind2,int start,int pageSize);

    @Query(value = "select *,(select count(*) from products where kind=? and product_name like %?%)totalcount from products where kind=? and product_name like %?% order by pid asc limit ?,?",nativeQuery = true)
    List<getProductInter> findByKindWithKeywordNative(String kind,String keyword,String kind2,String keyword2,int start,int pageSize);

    @Query(value = "SELECT A.*, B.*,c.*,d.* FROM products A left JOIN (SELECT * FROM coupons  where coupon_name=? )B ON 1 = 1 left JOIN (SELECT * FROM codes  where code_name=? )c ON 1 = 1 left JOIN (SELECT * FROM points  where po_email=? )d ON 1 = 1  where a.product_name=?",nativeQuery = true)
    getEventsAndProducts findProductJoinEvents(String couponName,String codeName,String email,String productName);
}
