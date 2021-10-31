package com.example.demo.product.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface productDao extends JpaRepository<productVo,Integer> {

    @Query(value = "select *,(select count(*) from products where kind=?)totalcount from products where kind=?  order by pid asc limit ?,?",nativeQuery = true)
    List<getProductInter> findByKind(String kind,String kind2,int start,int pageSize);

    @Query(value = "select *,(select count(*) from products where kind=? and product_name like %?%)totalcount from products where kind=? and product_name like %?% order by pid asc limit ?,?",nativeQuery = true)
    List<getProductInter> findByKindWithKeywordNative(String kind,String keyword,String kind2,String keyword2,int start,int pageSize);

    @Query(value = "SELECT a.*,d.* FROM products a left JOIN (SELECT * FROM points  where po_email=? )d ON 1 = 1  where a.pid=?",nativeQuery = true)
    getPointAndProducts findProductJoinPoints(String email,int productName);

    Optional<productVo> findByProductName(String productName);
    


}
